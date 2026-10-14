package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.RessourceIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(annotations = Controller.class)
public class GestionnaireExceptionsWeb {

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String gererRessourceIntrouvable(RessourceIntrouvableException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "erreurs/introuvable";
    }
}