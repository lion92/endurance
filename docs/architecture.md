# Architecture

```
navigateur ──► frontend (React, servi par nginx)
                 │  /api/*  (même origine : pas de CORS)
                 ▼
               backend (Spring Boot) ──► PostgreSQL
```

## Modèle de données
- `users` : id, email (unique), mot de passe haché (BCrypt), nom affiché, poids, objectif hebdo (minutes)
- `workouts` : id, user_id → users, sport, date, durée (min), distance (km, facultative),
  ressenti (1 à 10), calories (calculées côté serveur), notes

## API
| Méthode | Chemin | Rôle |
|---|---|---|
| POST | /api/auth/register | créer un compte |
| POST | /api/auth/login | se connecter (pose le cookie) |
| POST | /api/auth/logout | se déconnecter (efface le cookie) |
| GET | /api/me | l'utilisateur connecté |
| PUT | /api/me | modifier son profil |
| GET/POST | /api/workouts | lister (paginé) / créer |
| GET/PUT/DELETE | /api/workouts/{id} | lire / modifier / supprimer |
| GET | /api/stats/week | tableau de bord de la semaine |
