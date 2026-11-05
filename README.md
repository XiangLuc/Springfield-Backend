# Springfield-Backend

API REST de Springfield. Elle est faite avec Spring Boot et MariaDB.

Le frontend Angular est dans le dépôt [Springfield-Frontend](https://github.com/XiangLuc/Springfield-Frontend). Le détail du projet (choix techniques, tests, lot alpha) est dans [journal.md](journal.md).

## Prérequis

- IntelliJ IDEA
- Java 21
- MariaDB (par exemple avec XAMPP)

## Installation

1. Copier `.env.example` en `.env`, puis remplir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` et `JWT_SECRET` (au moins 32 caractères).
2. Créer la base et ses données de démonstration :

   ```bash
   mysql -u root -p < src/main/resources/script.sql
   ```

## Lancement

1. Ouvrir le projet dans IntelliJ et charger le `pom.xml` pour récupérer les dépendances Maven.
2. Lancer `SpringfieldApplication` depuis l'IDE, ou `./mvnw spring-boot:run` en ligne de commande.

L'API démarre sur http://localhost:8080 et sa documentation Swagger est sur http://localhost:8080/swagger-ui.html.

## Tests

```bash
./mvnw verify
```

MariaDB doit être démarré, car un test lance toute l'application.

## Comptes de test

| Profil | Identifiant | Mot de passe |
|---|---|---|
| Responsable d'exploitation | `resp.exploitation` | `motdepasse-Admin12345@?` |
| Agent | `agent.terrain` | `motdepasse-Admin12345@?` |

Application déployée : https://springfield-d105.onrender.com/
