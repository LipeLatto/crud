package br.edu.tarefas.logger;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Quem garante a instancia unica em cada versao?
 * Spring garante uma instancia por definicao de bean e container; @Lazy adia a criacao.
 * No classico, o holder estatico e a inicializacao de classes da JVM garantem a instancia.
 *
 * Quando o Singleton classico ainda faz sentido?
 * Quando nao existe container de injecao ou a biblioteca deve ser independente do Spring.
 * Aqui escolhemos Spring: injecao explicita facilita substituir o logger nos testes.
 */
@Component
@Lazy
public class AppLoggerSpring {
    public void debug(String mensagem) { SaidaLog.escrever("DEBUG", mensagem); }
    public void info(String mensagem) { SaidaLog.escrever("INFO", mensagem); }
    public void erro(String mensagem) { SaidaLog.escrever("ERRO", mensagem); }
}
