# API de Tarefas

Trabalho bimestral de Spring Boot com CRUD de tarefas e aplicação do padrão Singleton em um logger.

Usei Java 17, Spring Boot, Spring Data JPA e H2. O código está separado em Entity, Repository, Service e Controller. A lógica das operações fica no Service.

## Como rodar

É necessário ter o JDK 17 ou superior instalado. Depois de clonar o projeto:

```sh
git clone https://github.com/LipeLatto/crud.git
cd crud
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```sh
sh mvnw spring-boot:run
```

A API fica em `http://localhost:8080`. O banco H2 é criado na pasta `data`, então as tarefas continuam salvas depois de reiniciar a aplicação.

## Rotas

| Método | Rota | Retorno |
|---|---|---|
| POST | `/tarefas` | 201 Created |
| GET | `/tarefas` | 200 OK com a lista |
| GET | `/tarefas/{id}` | 200 OK ou 404 Not Found |
| PUT | `/tarefas/{id}` | 200 OK ou 404 Not Found |
| DELETE | `/tarefas/{id}` | 204 No Content ou 404 Not Found |

Para criar ou atualizar uma tarefa, envie um JSON como este pelo Postman:

```json
{
  "titulo": "Estudar Singleton",
  "concluida": false
}
```

Os dois campos são obrigatórios. O título pode ter até 120 caracteres e o id é gerado pelo banco. Requisições inválidas retornam 400. Para consultar, atualizar ou excluir, use o id retornado na criação.

## Logger

Implementei duas versões para comparar:

- `AppLogger`: Singleton clássico, sem anotações do Spring. Tem construtor privado e método estático `getInstance()`. Usei um holder interno para criar a instância somente no primeiro acesso, de forma segura entre threads.
- `AppLoggerSpring`: versão com `@Component`, recebida por injeção no Service e no Controller.

As duas escrevem no console e no arquivo `app.log`, na pasta de execução. A escrita é sincronizada para evitar que várias threads misturem as linhas.

O nível DEBUG registra detalhes da operação, como rota e id. INFO registra a conclusão, e ERRO registra recursos não encontrados ou exceções. Não são registrados títulos das tarefas, senhas, tokens ou outros dados sensíveis.

### Quem garante a instância única em cada versão?

No Singleton clássico, a inicialização do holder pela JVM garante uma instância por ClassLoader.
No Spring, o container mantém uma instância por definição de bean em cada ApplicationContext.

### Quando o Singleton clássico ainda faz sentido?

Quando o programa não usa um container de injeção, como uma aplicação de linha de comando.
Também pode ser usado em uma biblioteca que precisa funcionar sem depender do Spring.

### Versão escolhida

Escolhi a versão Spring para usar nas rotas, porque o projeto já usa injeção de dependências e isso facilita trocar o logger nos testes. O Singleton clássico ficou no projeto para comparação e tem um teste próprio. As respostas acima também estão nos comentários das classes.

## Testes

Para executar no Windows:

```powershell
.\mvnw.cmd test
```

No Linux ou macOS, use `sh mvnw test`.

Os 6 testes passaram. Eles verificam o CRUD, os status HTTP, as entradas inválidas, o tratamento de exceções e as instâncias dos loggers. O teste do Singleton clássico faz 64 chamadas concorrentes. Os testes usam um banco em memória separado do banco da aplicação.

O arquivo `app.log` incluído no repositório foi gerado durante os testes e serve como exemplo para a entrega.

## Referências

- [Repositório das aulas](https://github.com/gabrielmqc/api-spring-aulas)
- [Documentação do Spring sobre escopos de beans](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html)
