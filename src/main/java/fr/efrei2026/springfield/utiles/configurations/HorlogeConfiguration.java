package fr.efrei2026.springfield.utiles.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

// L'horloge est injectée plutôt que d'appeler LocalDateTime.now() : les tests peuvent ainsi la figer
@Configuration
public class HorlogeConfiguration {

    @Bean
    public Clock horloge() {
        return Clock.systemDefaultZone();
    }
}
