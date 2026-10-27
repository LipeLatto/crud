# API de Tarefas — CRUD e Singleton

Projeto do trabalho bimestral: Java 17+, Spring Boot 4.0.3, Maven 3.6.3+, Spring MVC, JPA e H2. O banco H2 fica em `data/` e preserva as tarefas entre execuções. O projeto usa a estrutura Entity → Repository → Service → Controller, com a lógica e as transações no Service.

## Executar

Na pasta deste projeto, com JDK instalado, use o Maven Wrapper (baixa o Maven automaticamente). No PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

No Linux/macOS use `sh mvnw test` e `sh mvnw spring-boot:run`. Com Maven já instalado, os comandos equivalentes são:

```sh
mvn test
mvn spring-boot:run
```

A API atende em `http://localhost:8080`. Alternativamente: `mvn package` e `java -jar target/api-tarefas-1.0.0.jar`. Não é necessário instalar um banco externo. Os testes usam H2 em memória e não alteram o banco da aplicação.

## Rotas e exemplos

| Método | Rota | Sucesso | Recurso inexistente |
|---|---|---|---|
| POST | `/tarefas` | 201, corpo e header Location | — |
| GET | `/tarefas` | 200, lista (inclusive vazia) | — |
| GET | `/tarefas/{id}` | 200, tarefa | 404 |
| PUT | `/tarefas/{id}` | 200, tarefa atualizada | 404 |
| DELETE | `/tarefas/{id}` | 204, sem corpo | 404 |

POST e PUT recebem os dois campos abaixo. O título é obrigatório e tem até 120 caracteres; `concluida` é obrigatório e booleano. Dados inválidos retornam 400. O id é gerado pelo banco.

```json
{"titulo":"Estudar Singleton","concluida":false}
```

Exemplos em terminal com curl (no Windows, use `curl.exe` ou um cliente como Postman):

```sh
curl -i -X POST http://localhost:8080/tarefas -H "Content-Type: application/json" -d '{"titulo":"Estudar Singleton","concluida":false}'
curl -i http://localhost:8080/tarefas
curl -i http://localhost:8080/tarefas/1
curl -i -X PUT http://localhost:8080/tarefas/1 -H "Content-Type: application/json" -d '{"titulo":"Entregar trabalho","concluida":true}'
curl -i -X DELETE http://localhost:8080/tarefas/1
curl -i http://localhost:8080/tarefas/1
```

Use o id retornado pelo POST. PUT substitui os campos da tarefa existente; não cria um recurso se o id não existir.

## Logger Singleton clássico

`src/main/java/br/edu/tarefas/logger/AppLogger.java` tem construtor privado, classe final e `getInstance()` estático. O holder interno só é inicializado no primeiro acesso ao método: a inicialização de classes da JVM garante publicação segura e uma instância por ClassLoader, mesmo com várias threads. Não há anotações Spring nessa classe.

```java
AppLogger logger = AppLogger.getInstance();
logger.debug("Operacao de demonstracao; id=1");
logger.info("Operacao concluida; id=1");
logger.erro("Tarefa nao encontrada; id=999");
```

O teste `AppLoggerTest` demonstra os três níveis e solicita a instância em 64 operações concorrentes. A escrita é sincronizada, em UTF-8, no console e em `app.log`, relativo à pasta de execução. O arquivo é aberto e fechado por escrita; se estiver indisponível, o erro aparece em stderr, sem interromper o CRUD. Não há rotação de arquivos nesta implementação didática.

## Cópia Spring e decisão final

`AppLoggerSpring.java` copia a interface de níveis usando a mesma infraestrutura de saída, com `@Component` e `@Lazy`. Service, Controller e tratamento de erros recebem esse bean por construtor. `@Lazy` adia a criação até a primeira solicitação do bean; como os consumidores são eager, isso pode ocorrer durante a inicialização da aplicação.

**Quem garante a instância única em cada versão?**
No clássico, o holder e a inicialização de classes da JVM garantem uma instância por ClassLoader.
No Spring, o container mantém uma instância por definição de bean em cada ApplicationContext; não é uma unicidade global da JVM.

**Quando o Singleton clássico ainda faz sentido?**
Em programas sem um container de injeção, como ferramentas de linha de comando.
Também em bibliotecas independentes do Spring que precisam controlar esse ciclo de vida.

As respostas também estão nos comentários das classes, conforme solicitado pelo enunciado.

**Versão final escolhida: Spring.** A API já usa injeção de dependências: receber o logger por construtor deixa a dependência explícita e facilita substituí-lo em testes. Somente `AppLoggerSpring` é usado nas rotas finais; o clássico permanece como implementação comparativa, executada apenas no seu teste. Nenhum bean Spring delega para `AppLogger.getInstance()`.

DEBUG registra método/rota, ids e detalhes não sensíveis; INFO registra operações concluídas; ERRO registra ausência de recurso e exceções. Títulos, corpos de requisições, senhas, tokens, CPF, mensagens de exceções e stacktraces não são enviados ao logger da aplicação. Quebras de linha são removidas antes da escrita. A sincronização da saída é explícita: o escopo singleton do Spring por si só não torna métodos thread-safe.

## Verificação

`mvn test` executa testes de integração com MVC e JPA reais, verificando o ciclo CRUD, persistência das atualizações, lista vazia, header Location, os três casos de 404 e validações 400. Também verifica o bean Spring, concorrência do clássico, níveis, linhas completas no arquivo e retorno 500 sem vazamento da mensagem da exceção.

O `app.log` incluído é um exemplo gerado pela execução dos testes. Novas execuções acrescentam linhas ao arquivo. Mensagens "Service: ..." ocorrem após o flush quando aplicável; a confirmação HTTP no Controller ocorre depois de o interceptor transacional concluir a chamada ao Service.

## Repositório para entrega

[LipeLatto/crud](https://github.com/LipeLatto/crud). Para obter uma cópia:

```sh
git clone https://github.com/LipeLatto/crud.git
cd crud
```

O `.gitignore` exclui banco local e arquivos de build, mantendo `app.log` para a avaliação.

Referências: [repositório indicado pelo professor](https://github.com/gabrielmqc/api-spring-aulas) e [escopos de beans na documentação Spring](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html).
