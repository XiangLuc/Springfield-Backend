package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.EvaluationInterditeException;
import fr.efrei2026.springfield.exceptions.RessourceIntrouvableException;
import fr.efrei2026.springfield.shared.dto.ErreurApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GestionnaireExceptionsWeb {

    private static final Logger log = LoggerFactory.getLogger(GestionnaireExceptionsWeb.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurApi> gererRessourceIntrouvable(RessourceIntrouvableException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurApi(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(EvaluationInterditeException.class)
    public ResponseEntity<ErreurApi> gererEvaluationInterdite(EvaluationInterditeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErreurApi(HttpStatus.CONFLICT, exception.getMessage()));
    }

    // ex. : GET /equipements/abc → l'id n'est pas un entier
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurApi> gererParametreInvalide(MethodArgumentTypeMismatchException exception) {
        String message = "Le paramètre '" + exception.getName() + "' a une valeur invalide : " + exception.getValue();
        return ResponseEntity.badRequest()
                .body(new ErreurApi(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurApi> gererErreurInattendue(Exception exception) {
        log.error("Erreur inattendue", exception);
        return ResponseEntity.internalServerError()
                .body(new ErreurApi(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur"));
    }
}