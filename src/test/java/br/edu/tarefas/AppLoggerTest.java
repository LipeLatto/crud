package br.edu.tarefas;

import br.edu.tarefas.logger.AppLogger;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

class AppLoggerTest {
    @Test void singletonConcorrenteEscreveTodosOsNiveis() throws Exception {
        assertTrue(Modifier.isPrivate(AppLogger.class.getDeclaredConstructor().getModifiers()));
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch inicio = new CountDownLatch(1);
        String marcador = "teste-" + System.nanoTime();
        try {
            List<Future<AppLogger>> resultados = IntStream.range(0, 64).mapToObj(i -> pool.submit(() -> {
                inicio.await();
                AppLogger logger = AppLogger.getInstance();
                logger.debug(marcador + "; operacao=" + i);
                return logger;
            })).toList();
            inicio.countDown();
            AppLogger unica = resultados.get(0).get(10, TimeUnit.SECONDS);
            for (Future<AppLogger> futuro : resultados) assertSame(unica, futuro.get(10, TimeUnit.SECONDS));
            unica.info(marcador + "; concluido");
            unica.erro(marcador + "; exemplo de recurso inexistente");
            unica.debug(marcador + "\nlinha-injetada");
            String log = Files.readString(Path.of("app.log"));
            assertTrue(log.contains("[INFO] " + marcador));
            assertTrue(log.contains("[ERRO] " + marcador));
            assertFalse(log.contains("\nlinha-injetada"));
            assertEquals(64, log.lines().filter(l -> l.contains(marcador + "; operacao=")).count());
        } finally { pool.shutdownNow(); }
    }
}
