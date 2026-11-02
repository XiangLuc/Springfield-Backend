package fr.efrei2026.springfield.exceptions;

import fr.efrei2026.springfield.shared.dto.ErreurApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GestionnaireErreurs {

    private static final Logger log = LoggerFactory.getLogger(GestionnaireErreurs.class);

    private static final String MESSAGE_CORPS_ILLISIBLE = "Le corps de la requête est absent ou mal formé.";
    private static final String MESSAGE_NON_CONNECTE = "Vous devez être connecté pour accéder à cette ressource.";
    private static final String MESSAGE_ACCES_REFUSE = "Vous n'avez pas les droits nécessaires pour effectuer cette action.";
    private static final String MESSAGE_JETON_CSRF_INVALIDE = "Requête refusée : le jeton de sécurité est absent ou invalide. Rechargez la page.";
    private static final String MESSAGE_ROUTE_INCONNUE = "La ressource demandée n'existe pas.";
    private static final String MESSAGE_METHODE_NON_AUTORISEE = "Cette opération n'est pas autorisée sur cette ressource.";
    private static final String MESSAGE_ERREUR_INATTENDUE = "Erreur interne du serveur.";

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurApi> gererRessourceIntrouvable(RessourceIntrouvableException exception) {
        return reponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ErreurApi> gererRegleMetierEnfreinte(RegleMetierException exception) {
        return reponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(IdentifiantInvalideException.class)
    public ResponseEntity<ErreurApi> gererIdentifiantsInvalides(IdentifiantInvalideException exception) {
        return reponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(LoginExistantException.class)
    public ResponseEntity<ErreurApi> gererLoginDejaUtilise(LoginExistantException exception) {
        return reponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurApi> gererDonneesInvalides(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    // ex. : GET /api/equipements/abc → l'id n'est pas un entier
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurApi> gererParametreInvalide(MethodArgumentTypeMismatchException exception) {
        String message = "Le paramètre '" + exception.getName() + "' a une valeur invalide : " + exception.getValue();
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurApi> gererCorpsIllisible() {
        return reponse(HttpStatus.BAD_REQUEST, MESSAGE_CORPS_ILLISIBLE);
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

    // Sans ce handler, une route inconnue tomberait dans le catch-all et produirait une 500
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurApi> gererRouteInconnue() {
        return reponse(HttpStatus.NOT_FOUND, MESSAGE_ROUTE_INCONNUE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErreurApi> gererMethodeNonAutorisee() {
        return reponse(HttpStatus.METHOD_NOT_ALLOWED, MESSAGE_METHODE_NON_AUTORISEE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurApi> gererErreurInattendue(Exception exception) {
        log.error("Erreur inattendue", exception);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE_ERREUR_INATTENDUE);
    }

    private ResponseEntity<ErreurApi> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(new ErreurApi(statut, message));
    }
}
