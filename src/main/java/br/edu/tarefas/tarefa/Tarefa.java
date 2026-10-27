package br.edu.tarefas.tarefa;

import jakarta.persistence.*;

@Entity
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String titulo;
    @Column(nullable = false)
    private boolean concluida;

    protected Tarefa() { }
    public Tarefa(String titulo, boolean concluida) {
        this.titulo = titulo;
        this.concluida = concluida;
    }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isConcluida() { return concluida; }
    public void atualizar(String titulo, boolean concluida) {
        this.titulo = titulo;
        this.concluida = concluida;
    }
}
