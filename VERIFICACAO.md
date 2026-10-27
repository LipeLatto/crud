# Verificação executada

Resultado: **BUILD SUCCESS — 6 testes, 0 falhas, 0 erros, 0 ignorados**.

Executado em 27/10/2026 com OpenJDK 22, Maven 3.9.12 e Spring Boot 4.0.3. O código compila para Java 17.

| Classe | Testes | Evidência |
|---|---:|---|
| AppLoggerTest | 1 | Construtor privado, mesma instância em 64 chamadas concorrentes, DEBUG/INFO/ERRO em arquivo, 64 linhas preservadas e remoção de quebras de linha |
| CrudIntegrationTest | 4 | CRUD completo com JPA/H2 e MockMvc, Location, estado atualizado, DELETE sem corpo, três casos de 404, entradas inválidas 400 e bean Spring único |
| ErroInternoTest | 1 | Exceção retorna 500 e mensagem sensível não aparece no corpo nem no arquivo |

O `app.log` contém a saída real dessa execução. O teste de CRUD usa o contexto Spring e o banco em memória, com requisições simuladas pelo MockMvc; não é um teste HTTP por socket nem uma verificação de implantação em produção.

Para reproduzir: execute `mvn test`, ou o Maven Wrapper incluído, na pasta do projeto. O primeiro uso exige conexão para baixar dependências.
