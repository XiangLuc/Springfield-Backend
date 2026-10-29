package fr.efrei2026.springfield.utiles.exceptions;

import fr.efrei2026.springfield.utiles.exceptions.response.MessageErreurApi;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GestionnaireErreurs {

    private static final String MESSAGE_CORPS_ILLISIBLE = "Le corps de la requête est absent ou mal formé.";
    private static final String MESSAGE_NON_CONNECTE = "Vous devez être connecté pour accéder à cette ressource.";
    private static final String MESSAGE_ACCES_REFUSE = "Vous n'avez pas les droits nécessaires pour effectuer cette action.";
    private static final String MESSAGE_JETON_CSRF_INVALIDE = "Requête refusée : le jeton de sécurité est absent ou invalide. Rechargez la page.";

    @ExceptionHandler(IdentifiantInvalideException.class)
    public ResponseEntity<MessageErreurApi> gererIdentifiantsInvalides(IdentifiantInvalideException exception) {
        return reponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(UtilisateurIntrouvableException.class)
    public ResponseEntity<MessageErreurApi> gererUtilisateurIntrouvable(UtilisateurIntrouvableException exception) {
        return reponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(LoginExistantException.class)
    public ResponseEntity<MessageErreurApi> gererLoginDejaUtilise(LoginExistantException exception) {
        return reponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageErreurApi> gererDonneesInvalides(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageErreurApi> gererCorpsIllisible() {
        return reponse(HttpStatus.BAD_REQUEST, MESSAGE_CORPS_ILLISIBLE);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MessageErreurApi> gererUtilisateurNonConnecte() {
        return reponse(HttpStatus.UNAUTHORIZED, MESSAGE_NON_CONNECTE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageErreurApi> gererAccesRefuse() {
        return reponse(HttpStatus.FORBIDDEN, MESSAGE_ACCES_REFUSE);
    }

    @ExceptionHandler(CsrfException.class)
    public ResponseEntity<MessageErreurApi> gererJetonCsrfInvalide() {
        return reponse(HttpStatus.FORBIDDEN, MESSAGE_JETON_CSRF_INVALIDE);
    }

    private ResponseEntity<MessageErreurApi> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(new MessageErreurApi(message));
    }
}
