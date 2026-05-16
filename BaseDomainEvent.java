import java.time.Instant;
import java.util.UUID;

// 1. Contrato Base: Garante que todo evento do sistema tenha um ID e uma data/hora
public interface DomainEvent {
    UUID eventId();
    Instant occurredOn();
}

// 2. Abstração (Classe Base): Encapsula a lógica repetitiva de geração de ID e Timestamp
public abstract class BaseDomainEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;

    protected BaseDomainEvent() {
        // Todo evento nasce com um ID único e o momento exato da sua criação
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}

// 3. Exemplo Prático: Um evento concreto estendendo a abstração
public class ConsultaAgendadaEvent extends BaseDomainEvent {

    // Dados específicos do fato ocorrido (sempre imutáveis, marcados como 'final')
    private final UUID consultaId;
    private final UUID petId;

    public ConsultaAgendadaEvent(UUID consultaId, UUID petId) {
        super(); // Invoca o construtor da classe abstrata para gerar o ID e Timestamp
        this.consultaId = consultaId;
        this.petId = petId;
    }

    public UUID getConsultaId() {
        return consultaId;
    }

    public UUID getPetId() {
        return petId;
    }
}