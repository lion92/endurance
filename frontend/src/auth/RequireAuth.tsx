import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from './authContext'

export function RequireAuth() {
  const { state } = useAuth()
  const location = useLocation()

  if (state.status === 'loading') return <p className="muted center">Chargement…</p>
  if (state.status === 'anonymous') {
    return <Navigate to="/connexion" replace state={{ from: location.pathname }} />
  }
  return <Outlet />
}
