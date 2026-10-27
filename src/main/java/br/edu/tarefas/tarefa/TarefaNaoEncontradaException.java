package br.edu.tarefas.tarefa;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException() { super("Tarefa nao encontrada"); }
}
