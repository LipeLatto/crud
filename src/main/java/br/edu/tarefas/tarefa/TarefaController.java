package br.edu.tarefas.tarefa;

import br.edu.tarefas.logger.AppLoggerSpring;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {
    private final TarefaService service;
    private final AppLoggerSpring logger;

    public TarefaController(TarefaService service, AppLoggerSpring logger) {
        this.service = service;
        this.logger = logger;
    }

    @PostMapping
    public ResponseEntity<Tarefa> criar(@Valid @RequestBody TarefaRequest request) {
        logger.debug("Controller: POST /tarefas");
        Tarefa tarefa = service.criar(request);
        logger.info("Controller: POST /tarefas concluido; status=201; id=" + tarefa.getId());
        return ResponseEntity.created(URI.create("/tarefas/" + tarefa.getId())).body(tarefa);
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> listar() {
        logger.debug("Controller: GET /tarefas");
        List<Tarefa> tarefas = service.listar();
        logger.info("Controller: GET /tarefas concluido; status=200");
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscar(@PathVariable Long id) {
        logger.debug("Controller: GET /tarefas/{id}; id=" + id);
        Tarefa tarefa = service.buscar(id);
        logger.info("Controller: GET /tarefas/{id} concluido; status=200; id=" + id);
        return ResponseEntity.ok(tarefa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizar(@PathVariable Long id, @Valid @RequestBody TarefaRequest request) {
        logger.debug("Controller: PUT /tarefas/{id}; id=" + id);
        Tarefa tarefa = service.atualizar(id, request);
        logger.info("Controller: PUT /tarefas/{id} concluido; status=200; id=" + id);
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        logger.debug("Controller: DELETE /tarefas/{id}; id=" + id);
        service.excluir(id);
        logger.info("Controller: DELETE /tarefas/{id} concluido; status=204; id=" + id);
        return ResponseEntity.noContent().build();
    }
}
