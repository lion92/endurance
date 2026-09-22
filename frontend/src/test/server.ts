import { setupServer } from 'msw/node'

/** Aucun point d'API n'est simulé par défaut : chaque test déclare ceux dont il a besoin. */
export const server = setupServer()
