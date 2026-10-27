package br.edu.tarefas;

import br.edu.tarefas.logger.AppLoggerSpring;
import br.edu.tarefas.tarefa.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired TarefaRepository repository;
    @Autowired ApplicationContext context;
    @Autowired AppLoggerSpring logger;

    @BeforeEach void limpar() { repository.deleteAll(); }

    @Test void cicloCrudCompleto() throws Exception {
        mvc.perform(get("/tarefas")).andExpect(status().isOk()).andExpect(content().json("[]"));
        String location = mvc.perform(post("/tarefas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Estudar Singleton\",\"concluida\":false}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Estudar Singleton"))
                .andReturn().getResponse().getHeader("Location");
        assertNotNull(location);
        mvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.concluida").value(false));
        mvc.perform(get("/tarefas")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Entregar trabalho\",\"concluida\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.concluida").value(true));
        mvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.titulo").value("Entregar trabalho"));
        mvc.perform(delete(location)).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get(location)).andExpect(status().isNotFound());
        mvc.perform(get("/tarefas")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test void inexistentesRetornam404() throws Exception {
        mvc.perform(get("/tarefas/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/tarefas/999999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Teste\",\"concluida\":false}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/tarefas/999999")).andExpect(status().isNotFound());
    }

    @Test void entradasInvalidasRetornam400() throws Exception {
        for (String corpo : new String[]{"{}", "{\"titulo\":\" \",\"concluida\":false}",
                "{\"titulo\":\"Teste\",\"concluida\":null}", "{invalido"}) {
            mvc.perform(post("/tarefas").contentType(MediaType.APPLICATION_JSON).content(corpo))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/tarefas/abc")).andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test void springReutilizaBean() {
        assertSame(logger, context.getBean(AppLoggerSpring.class));
        assertEquals(1, context.getBeansOfType(AppLoggerSpring.class).size());
    }
}
