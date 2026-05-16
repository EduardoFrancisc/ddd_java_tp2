import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

// 1. O Evento de Domínio (Criado como um 'record' do Java para ser imutável)
public record ConsultaAgendadaEvent(UUID consultaId, UUID petId, UUID veterinarioId, LocalDateTime dataHorario) {}
public record ConsultaCanceladaEvent(UUID consultaId, String motivo) {}

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Referência a outro Agregado (Pet) APENAS pelo ID
    // Se o Pet for um microsserviço separado, isso é fundamental.
    @Column(name = "pet_id", nullable = false)
    private UUID petId;

    // Referência a outro Agregado (Veterinário) APENAS pelo ID
    @Column(name = "veterinario_id", nullable = false)
    private UUID veterinarioId;

    @Column(nullable = false)
    private LocalDateTime dataHorario;

    @Column(length = 500)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsulta status;

    // Construtor protegido exigido por frameworks ORM (como Hibernate/JPA)
    protected Consulta() {}

    public Consulta(UUID petId, UUID veterinarioId, LocalDateTime dataHorario) {
        // Proteção das Invariantes de Negócio no momento da criação
        if (petId == null || veterinarioId == null) {
            throw new IllegalArgumentException("Pet e Veterinário são obrigatórios para agendar uma consulta.");
        }
        if (dataHorario == null || dataHorario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da consulta deve ser no futuro.");
        }

        this.petId = petId;
        this.veterinarioId = veterinarioId;
        this.dataHorario = dataHorario;
        this.status = StatusConsulta.AGENDADA;
    }

    // Comportamentos de negócio substituem os "setters" anêmicos
    public void cancelar(String motivo) {
        // Outra Invariante: não se pode cancelar o que já aconteceu
        if (this.status == StatusConsulta.REALIZADA) {
            throw new IllegalStateException("Não é possível cancelar uma consulta já realizada.");
        }
        this.status = StatusConsulta.CANCELADA;
        this.observacoes = motivo;
    }

    public void registrarAtendimento(String observacoesClinicas) {
        if (this.status != StatusConsulta.AGENDADA) {
            throw new IllegalStateException("Apenas consultas agendadas podem receber atendimento.");
        }
        this.status = StatusConsulta.REALIZADA;
        this.observacoes = observacoesClinicas;
    }

    // Getters para expor o estado (omitidos para brevidade)
    public UUID getId() { return id; }
    public UUID getPetId() { return petId; }
    public UUID getVeterinarioId() { return veterinarioId; }
    public StatusConsulta getStatus() { return status; }
    
    public void cancelar(String motivo) {
        if (this.status == StatusConsulta.REALIZADA) {
            throw new IllegalStateException("Não é possível cancelar uma consulta já realizada.");
        }
        this.status = StatusConsulta.CANCELADA;

        // Registra o evento de cancelamento
        registerEvent(new ConsultaCanceladaEvent(this.id, motivo));
    }
}

enum StatusConsulta {
    AGENDADA, REALIZADA, CANCELADA
}