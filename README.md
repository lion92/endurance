# Endurance — une application de suivi sportif, de zéro

Projet pédagogique construit **pas à pas** en vidéo : chaque commit est une étape expliquée.

- **backend/** : API REST en Spring Boot 4.1 (Java 21), PostgreSQL, Flyway, Spring Security + JWT
- **frontend/** : application React 19 + TypeScript, construite avec Vite
- **docker-compose.yml** : la base de données, l'API et le site, en une commande

## Ce que fait l'application
- inscription, connexion, déconnexion — jeton JWT dans un cookie `HttpOnly`
- journal de séances : course, vélo, natation, musculation, marche…
- calories estimées à partir de l'intensité (MET) et du poids
- tableau de bord de la semaine : volume, distance, série de jours, objectif hebdomadaire

Suivez l'historique : `git log --oneline --reverse`.
