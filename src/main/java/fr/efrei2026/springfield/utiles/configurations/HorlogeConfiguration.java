package fr.efrei2026.springfield.utiles.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class HorlogeConfiguration {

    private static final ZoneId FUSEAU_HORAIRE = ZoneId.of("Europe/Paris");

    @Bean
    public Clock clock() {
        return Clock.system(FUSEAU_HORAIRE);
    }
}