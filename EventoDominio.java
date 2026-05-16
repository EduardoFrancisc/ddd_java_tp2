import java.time.Instant;
import java.util.UUID;

// 1. O contrato base que todo evento do sistema deve seguir
public interface EventoDominio {
    UUID idEvento();
    Instant dataOcorrencia();
}

// 2. A implementação do evento usando 'record'
public record VacinaAplicadaEvent(
        UUID idEvento,
        Instant dataOcorrencia,
        UUID idConsulta,
        UUID idPet,
        UUID idVacina,
        String nomeVacina
) implements EventoDominio {

    // Construtor customizado e compacto:
    // Facilita a criação do evento injetando automaticamente o ID único e o momento exato,
    // exigindo de quem cria o evento apenas os dados específicos do negócio.
    public VacinaAplicadaEvent(UUID idConsulta, UUID idPet, UUID idVacina, String nomeVacina) {
        this(
                UUID.randomUUID(),
                Instant.now(),
                idConsulta,
                idPet,
                idVacina,
                nomeVacina
        );
    }
}