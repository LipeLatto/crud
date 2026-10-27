package br.edu.tarefas;

import br.edu.tarefas.logger.AppLoggerSpring;
import br.edu.tarefas.tarefa.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ErroInternoTest {
    @Test void excecaoRetorna500SemVazarMensagem() throws Exception {
        TarefaService service = mock(TarefaService.class);
        AppLoggerSpring logger = new AppLoggerSpring();
        String segredo = "token-secreto-nao-deve-aparecer";
        when(service.listar()).thenThrow(new IllegalStateException(segredo));
        var mvc = MockMvcBuilders.standaloneSetup(new TarefaController(service, logger))
                .setControllerAdvice(new ApiExceptionHandler(logger)).build();
        mvc.perform(get("/tarefas")).andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"erro\":\"Erro interno\"}"));
        assertFalse(Files.readString(Path.of("app.log")).contains(segredo));
    }
}
