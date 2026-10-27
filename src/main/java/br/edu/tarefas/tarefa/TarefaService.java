package br.edu.tarefas.tarefa;

import br.edu.tarefas.logger.AppLoggerSpring;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TarefaService {
    private final TarefaRepository repository;
    private final AppLoggerSpring logger;

    public TarefaService(TarefaRepository repository, AppLoggerSpring logger) {
        this.repository = repository;
        this.logger = logger;
    }

    @Transactional
    public Tarefa criar(TarefaRequest request) {
        logger.debug("Service: criar tarefa; concluida=" + request.concluida());
        Tarefa tarefa = repository.saveAndFlush(new Tarefa(request.titulo(), request.concluida()));
        logger.info("Service: tarefa criada; id=" + tarefa.getId());
        return tarefa;
    }

    public List<Tarefa> listar() {
        logger.debug("Service: listar tarefas ordenadas por id");
        List<Tarefa> tarefas = repository.findAll(Sort.by("id"));
        logger.info("Service: listagem concluida; quantidade=" + tarefas.size());
        return tarefas;
    }

    public Tarefa buscar(Long id) {
        logger.debug("Service: buscar tarefa; id=" + id);
        Tarefa tarefa = exigir(id);
        logger.info("Service: consulta concluida; id=" + id);
        return tarefa;
    }

    @Transactional
    public Tarefa atualizar(Long id, TarefaRequest request) {
        logger.debug("Service: atualizar tarefa; id=" + id + "; concluida=" + request.concluida());
        Tarefa tarefa = exigir(id);
        tarefa.atualizar(request.titulo(), request.concluida());
        repository.saveAndFlush(tarefa);
        logger.info("Service: tarefa atualizada; id=" + id);
        return tarefa;
    }

    @Transactional
    public void excluir(Long id) {
        logger.debug("Service: excluir tarefa; id=" + id);
        repository.delete(exigir(id));
        repository.flush();
        logger.info("Service: tarefa excluida; id=" + id);
    }

    private Tarefa exigir(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            logger.erro("Service: tarefa nao encontrada; id=" + id);
            return new TarefaNaoEncontradaException();
        });
    }
}
