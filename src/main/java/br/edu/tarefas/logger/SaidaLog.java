package br.edu.tarefas.logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;

// Infraestrutura compartilhada: nenhuma instancia Singleton e criada aqui.
final class SaidaLog {
    private SaidaLog() { }

    // Evita intercalar linhas entre threads. Abre/fecha o arquivo a cada escrita.
    static synchronized void escrever(String nivel, String mensagem) {
        String segura = mensagem.replace('\r', ' ').replace('\n', ' ');
        String linha = "%s [%s] %s%n".formatted(OffsetDateTime.now(), nivel, segura);
        System.out.print(linha);
        try {
            Files.writeString(Path.of("app.log"), linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Falha do log nao deve derrubar uma operacao de negocio.
            System.err.println("[ERRO] Nao foi possivel escrever em app.log: "
                    + e.getClass().getSimpleName());
        }
    }
}
