package br.edu.tarefas.logger;

/**
 * Quem garante a instancia unica em cada versao?
 * Aqui, a JVM inicializa Holder uma unica vez por ClassLoader, ao chamar getInstance().
 * Na copia Spring, o container gerencia uma instancia por bean e ApplicationContext.
 *
 * Quando o Singleton classico ainda faz sentido?
 * Em programas sem container de injecao, como ferramentas de linha de comando.
 * Tambem serve a bibliotecas independentes do Spring que precisam desse ciclo de vida.
 */
public final class AppLogger {
    private AppLogger() { }

    private static class Holder {
        private static final AppLogger INSTANCE = new AppLogger();
    }

    public static AppLogger getInstance() {
        return Holder.INSTANCE;
    }

    public void debug(String mensagem) { SaidaLog.escrever("DEBUG", mensagem); }
    public void info(String mensagem) { SaidaLog.escrever("INFO", mensagem); }
    public void erro(String mensagem) { SaidaLog.escrever("ERRO", mensagem); }
}
