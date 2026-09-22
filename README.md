# Endurance — une application de suivi sportif, construite de zéro

[![CI](https://github.com/lion92/endurance/actions/workflows/ci.yml/badge.svg)](https://github.com/lion92/endurance/actions/workflows/ci.yml)

Projet pédagogique construit **pas à pas**, en TDD : chaque commit est une étape expliquée.
`git log --oneline --reverse` se lit comme le sommaire de la vidéo.

| Dossier | Contenu |
|---|---|
| `backend/` | API REST — Spring Boot 4.1, Java 21, PostgreSQL 18, Flyway, Spring Security 7 (JWT) |
| `frontend/` | Site — React 19, TypeScript 6, Vite 8, React Router 8 |
| `e2e/` | Scénarios de bout en bout — Playwright |

## Ce que fait l'application
- **Compte** : inscription, connexion, déconnexion. Le jeton JWT voyage dans un cookie
  `HttpOnly` · `SameSite=Strict` · `Path=/api` — illisible par le JavaScript de la page.
- **Séances** : course, vélo, natation, musculation, marche, yoga ; durée, distance, ressenti,
  notes. Liste paginée, modification, suppression confirmée.
- **Calories** : estimées par le serveur — MET × poids × durée — et figées à l'enregistrement.
- **Tableau de bord** : volume de la semaine, distance, calories, minutes par jour, répartition
  par sport, progression vers l'objectif hebdomadaire, série de jours consécutifs.
- **Profil** : nom, poids, objectif (l'OMS recommande 150 min d'activité modérée par semaine).

## Démarrer en développement
```bash
docker compose up -d                    # PostgreSQL sur 127.0.0.1:5442
cd backend  && ./mvnw spring-boot:run   # API sur :8080
cd frontend && npm install && npm run dev   # site sur :5173 (proxy /api → :8080)
```

## Lancer toute la pile, comme en production
```bash
cp .env.example .env                    # puis : openssl rand -base64 32  →  JWT_SECRET
docker compose -f compose.prod.yaml up -d --build   # site sur 127.0.0.1:8090
```

## Les tests
```bash
cd backend  && ./mvnw verify   # 40 tests — PostgreSQL réel via Testcontainers
cd frontend && npm test        # 38 tests — Vitest, Testing Library, MSW
cd e2e      && npm test        # 2 scénarios Playwright contre la pile Docker
```

## Ce que le projet montre, au-delà des fonctionnalités
- **TDD** : chaque fonctionnalité commence par un test rouge, vérifié comme tel.
- **Sécurité** : cookie `HttpOnly`, CSRF façon SPA, haché leurre contre l'énumération par
  chronométrage, `404` plutôt que `403` sur la ressource d'autrui, base sans port publié.
- **Schéma** : Flyway seul maître, `ddl-auto=validate` en garde-fou.
- **Domaine testable** : le calcul des statistiques ne connaît ni la base, ni l'horloge.
