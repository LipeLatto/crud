package br.edu.tarefas.tarefa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarefaRequest(
        @NotBlank @Size(max = 120) String titulo,
        @NotNull Boolean concluida) { }
