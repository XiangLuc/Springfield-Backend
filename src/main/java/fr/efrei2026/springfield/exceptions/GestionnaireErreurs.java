package fr.efrei2026.springfield.exceptions;

import fr.efrei2026.springfield.shared.dto.ErreurApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireErreurs {

    private static final Logger log = LoggerFactory.getLogger(GestionnaireErreurs.class);

    private static final String MESSAGE_CORPS_ILLISIBLE = "Le corps de la requête est absent ou mal formé.";
    private static final String MESSAGE_NON_CONNECTE = "Vous devez être connecté pour accéder à cette ressource.";
    private static final String MESSAGE_ACCES_REFUSE = "Vous n'avez pas les droits nécessaires pour effectuer cette action.";
    private static final String MESSAGE_JETON_CSRF_INVALIDE = "Requête refusée : le jeton de sécurité est absent ou invalide. Rechargez la page.";
    private static final String MESSAGE_ROUTE_INEXISTANTE = "La ressource demandée n'existe pas.";
    private static final String MESSAGE_TYPE_DE_CONTENU_NON_SUPPORTE = "Le type de contenu de la requête n'est pas pris en charge.";
    private static final String MESSAGE_ERREUR_INATTENDUE = "Erreur interne du serveur";

    @ExceptionHandler(IdentifiantInvalideException.class)
    public ResponseEntity<ErreurApi> gererIdentifiantsInvalides(IdentifiantInvalideException exception) {
        return reponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurApi> gererRessourceIntrouvable(RessourceIntrouvableException exception) {
        return reponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({LoginExistantException.class, EvaluationInterditeException.class, IncidentDejaResoluException.class})
    public ResponseEntity<ErreurApi> gererConflit(RuntimeException exception) {
        return reponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurApi> gererDonneesInvalides(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurApi> gererCorpsIllisible() {
        return reponse(HttpStatus.BAD_REQUEST, MESSAGE_CORPS_ILLISIBLE);
    }

    // ex. : GET /equipements/abc → l'id n'est pas un entier
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurApi> gererParametreInvalide(MethodArgumentTypeMismatchException exception) {
        String message = "Le paramètre '" + exception.getName() + "' a une valeur invalide : " + exception.getValue();
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErreurApi> gererUtilisateurNonConnecte() {
        return reponse(HttpStatus.UNAUTHORIZED, MESSAGE_NON_CONNECTE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErreurApi> gererAccesRefuse() {
        return reponse(HttpStatus.FORBIDDEN, MESSAGE_ACCES_REFUSE);
    }

    @ExceptionHandler(CsrfException.class)
    public ResponseEntity<ErreurApi> gererJetonCsrfInvalide() {
        return reponse(HttpStatus.FORBIDDEN, MESSAGE_JETON_CSRF_INVALIDE);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurApi> gererRouteInexistante() {
        return reponse(HttpStatus.NOT_FOUND, MESSAGE_ROUTE_INEXISTANTE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErreurApi> gererMethodeNonAutorisee(HttpRequestMethodNotSupportedException exception) {
        String message = "La méthode " + exception.getMethod() + " n'est pas autorisée sur cette ressource.";
        return reponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErreurApi> gererTypeDeContenuNonSupporte() {
        return reponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, MESSAGE_TYPE_DE_CONTENU_NON_SUPPORTE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurApi> gererErreurInattendue(Exception exception) {
        log.error("Erreur inattendue", exception);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE_ERREUR_INATTENDUE);
    }

    private ResponseEntity<ErreurApi> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(new ErreurApi(message));
    }
}
