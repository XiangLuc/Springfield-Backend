package fr.efrei2026.springfield.shared.dto;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public record ErreurApi(int statut, String message, LocalDateTime horodatage) {

    public ErreurApi(HttpStatus statut, String message) {
        this(statut.value(), message, LocalDateTime.now());
    }
}