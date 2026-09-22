import { useEffect, useMemo, useState, type ReactNode } from 'react'
import { authApi, profileApi, type Credentials, type Registration } from '../api/endpoints'
import { ApiError } from '../api/http'
import type { User } from '../api/types'
import { AuthContext, type Auth, type AuthState } from './authContext'

/**
 * Le navigateur garde le jeton dans un cookie HttpOnly que NOUS ne pouvons pas lire.
 * Pour savoir si l'on est connecté, on pose donc la question au serveur : GET /api/me.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>({ status: 'loading' })

  useEffect(() => {
    let active = true
    profileApi
      .me()
      .then((user) => active && setState({ status: 'authenticated', user }))
      .catch((error: unknown) => {
        if (!active) return
        if (error instanceof ApiError && error.status === 401) setState({ status: 'anonymous' })
        else throw error
      })
    return () => {
      active = false
    }
  }, [])

  const auth = useMemo<Auth>(() => {
    const authenticated = (user: User) => setState({ status: 'authenticated', user })
    return {
      state,
      login: async (credentials: Credentials) => authenticated(await authApi.login(credentials)),
      register: async (registration: Registration) => {
        await authApi.register(registration)
        authenticated(await authApi.login(registration))
      },
      logout: async () => {
        await authApi.logout()
        setState({ status: 'anonymous' })
      },
      replaceUser: authenticated,
    }
  }, [state])

  return <AuthContext value={auth}>{children}</AuthContext>
}
