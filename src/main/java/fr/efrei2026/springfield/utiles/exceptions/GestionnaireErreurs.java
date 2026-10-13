package fr.efrei2026.springfield.utiles.exceptions;

import fr.efrei2026.springfield.utiles.exceptions.response.MessageErreurApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler(IdentifiantInvalideException.class)
    public ResponseEntity<MessageErreurApi> gererIdentifiantsInvalides(IdentifiantInvalideException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageErreurApi(exception.getMessage()));
    }

    @ExceptionHandler(LoginExistantException.class)
    public ResponseEntity<MessageErreurApi> gererLoginDejaUtilise(LoginExistantException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageErreurApi(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageErreurApi> gererDonneesInvalides(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageErreurApi(message));
    }
}
