package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.EvaluationInterditeException;
import fr.efrei2026.springfield.exceptions.IncidentDejaResoluException;
import fr.efrei2026.springfield.exceptions.RessourceIntrouvableException;
import fr.efrei2026.springfield.shared.dto.ErreurApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(IncidentDejaResoluException.class)
    public ResponseEntity<ErreurApi> gererIncidentDejaResolu(IncidentDejaResoluException exception) {
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

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurApi> gererRouteInexistante() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurApi(HttpStatus.NOT_FOUND, "La ressource demandée n'existe pas."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErreurApi> gererMethodeNonAutorisee(HttpRequestMethodNotSupportedException exception) {
        String message = "La méthode " + exception.getMethod() + " n'est pas autorisée sur cette ressource.";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErreurApi(HttpStatus.METHOD_NOT_ALLOWED, message));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErreurApi> gererTypeDeContenuNonSupporte() {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErreurApi(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Le type de contenu de la requête n'est pas pris en charge."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurApi> gererErreurInattendue(Exception exception) {
        log.error("Erreur inattendue", exception);
        return ResponseEntity.internalServerError()
                .body(new ErreurApi(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur"));
    }
}