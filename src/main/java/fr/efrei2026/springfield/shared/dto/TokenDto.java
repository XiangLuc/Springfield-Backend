package fr.efrei2026.springfield.shared.dto;

public class TokenDto {

    private final String token;

    public TokenDto(String token) {
        this.token = token;
    }

    public String getJeton() {
        return token;
    }
}
