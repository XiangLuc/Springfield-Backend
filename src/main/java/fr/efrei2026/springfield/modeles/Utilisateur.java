package fr.efrei2026.springfield.modeles;

import fr.efrei2026.springfield.modeles.enums.Profil;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "utilisateur", schema = "springfield_bdd")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Basic
    @Column(name = "login", nullable = false, unique = true)
    private String login;
    @Basic
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    @Basic
    @Column(name = "prenom", nullable = false)
    private String prenom;
    @Enumerated(EnumType.STRING)
    @Column(name = "profil", nullable = false)
    private Profil profil;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return id == that.id && Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }
}
