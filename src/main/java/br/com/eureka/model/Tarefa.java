package br.com.eureka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "tarefa")
public class Tarefa extends RegistroExcluivel {

    public static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_prevista_entrega", nullable = false)
    private LocalDate dataPrevistaEntrega;

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusTarefa status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    protected Tarefa() {
    }

    private Tarefa(String nome, LocalDate dataCriacao, LocalDate dataPrevistaEntrega, Disciplina disciplina) {
        this.nome = textoObrigatorio(nome, "nome");
        this.dataCriacao = validarDataCriacao(dataCriacao);
        this.dataPrevistaEntrega = validarDataPrevistaEntrega(dataPrevistaEntrega, this.dataCriacao);
        this.status = StatusTarefa.PENDENTE;
        this.disciplina = validarDisciplina(disciplina);
    }

    public static Tarefa criar(String nome, LocalDate dataPrevistaEntrega, Disciplina disciplina, Clock clock) {
        LocalDate dataCriacao = LocalDate.now(clock.withZone(TIME_ZONE));
        return new Tarefa(nome, dataCriacao, dataPrevistaEntrega, disciplina);
    }

    @PrePersist
    void prepararPersistencia() {
        if (dataCriacao == null) {
            dataCriacao = LocalDate.now(TIME_ZONE);
        }
        if (status == null) {
            status = StatusTarefa.PENDENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public LocalDate getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void entregarEm(LocalDate dataEntrega) {
        if (status != StatusTarefa.PENDENTE) {
            throw new IllegalStateException("Tarefa ja foi entregue");
        }
        LocalDate entregaValidada = validarDataEntrega(dataEntrega, dataCriacao);
        this.dataEntrega = entregaValidada;
        this.status = entregaValidada.isAfter(dataPrevistaEntrega)
                ? StatusTarefa.ENTREGUE_COM_ATRASO
                : StatusTarefa.ENTREGUE;
    }

    public SituacaoPrazoTarefa calcularSituacaoPrazo(LocalDate referencia) {
        if (status == StatusTarefa.PENDENTE && referencia.isAfter(dataPrevistaEntrega)) {
            return SituacaoPrazoTarefa.VENCIDA;
        }
        return SituacaoPrazoTarefa.NO_PRAZO;
    }

    public boolean estaVencida(LocalDate referencia) {
        return calcularSituacaoPrazo(referencia) == SituacaoPrazoTarefa.VENCIDA;
    }

    private static LocalDate validarDataCriacao(LocalDate dataCriacao) {
        if (dataCriacao == null) {
            throw new IllegalArgumentException("dataCriacao e obrigatoria");
        }
        return dataCriacao;
    }

    private static LocalDate validarDataPrevistaEntrega(LocalDate dataPrevistaEntrega, LocalDate dataCriacao) {
        if (dataPrevistaEntrega == null) {
            throw new IllegalArgumentException("dataPrevistaEntrega e obrigatoria");
        }
        if (dataPrevistaEntrega.isBefore(dataCriacao)) {
            throw new IllegalArgumentException("dataPrevistaEntrega nao pode ser anterior a dataCriacao");
        }
        return dataPrevistaEntrega;
    }

    private static LocalDate validarDataEntrega(LocalDate dataEntrega, LocalDate dataCriacao) {
        if (dataEntrega == null) {
            throw new IllegalArgumentException("dataEntrega e obrigatoria");
        }
        if (dataEntrega.isBefore(dataCriacao)) {
            throw new IllegalArgumentException("dataEntrega nao pode ser anterior a dataCriacao");
        }
        return dataEntrega;
    }

    private static Disciplina validarDisciplina(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("disciplina e obrigatoria");
        }
        return disciplina;
    }
}
