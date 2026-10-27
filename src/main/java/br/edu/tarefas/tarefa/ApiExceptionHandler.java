package br.edu.tarefas.tarefa;

import br.edu.tarefas.logger.AppLoggerSpring;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private final AppLoggerSpring logger;
    public ApiExceptionHandler(AppLoggerSpring logger) { this.logger = logger; }

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> naoEncontrada() {
        logger.erro("Controller: tarefa nao encontrada; status=404");
        return ResponseEntity.status(404).body(Map.of("erro", "Tarefa nao encontrada"));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> invalida(Exception e) {
        logger.erro("Controller: requisicao invalida; status=400; tipo=" + e.getClass().getSimpleName());
        return ResponseEntity.badRequest().body(Map.of("erro", "Requisicao invalida. Verifique o id, titulo e concluida."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperada(Exception e) {
        // Nao registra mensagem/stacktrace: podem conter SQL e dados do usuario.
        logger.erro("Controller: falha inesperada; status=500; tipo=" + e.getClass().getSimpleName());
        return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno"));
    }
}
