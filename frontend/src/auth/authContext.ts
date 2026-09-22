import { createContext, useContext } from 'react'
import type { Credentials, Registration } from '../api/endpoints'
import type { User } from '../api/types'

export type AuthState =
  | { status: 'loading' }
  | { status: 'anonymous' }
  | { status: 'authenticated'; user: User }

export interface Auth {
  state: AuthState
  login: (credentials: Credentials) => Promise<void>
  register: (registration: Registration) => Promise<void>
  logout: () => Promise<void>
  replaceUser: (user: User) => void
}

export const AuthContext = createContext<Auth | null>(null)

export function useAuth(): Auth {
  const auth = useContext(AuthContext)
  if (!auth) throw new Error('useAuth doit être utilisé sous <AuthProvider>')
  return auth
}

/** Pour les pages protégées : là, l'utilisateur existe forcément. */
export function useCurrentUser(): User {
  const { state } = useAuth()
  if (state.status !== 'authenticated') throw new Error('Aucun utilisateur connecté')
  return state.user
}
