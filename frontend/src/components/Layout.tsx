import { NavLink, Outlet, useNavigate } from 'react-router'
import { useAuth, useCurrentUser } from '../auth/authContext'

export function Layout() {
  const user = useCurrentUser()
  const { logout } = useAuth()
  const navigate = useNavigate()

  async function onLogout() {
    await logout()
    navigate('/connexion', { replace: true })
  }

  return (
    <div className="shell">
      <header className="topbar">
        <span className="logo">Endurance</span>
        <nav>
          <NavLink to="/" end>Tableau de bord</NavLink>
          <NavLink to="/seances">Séances</NavLink>
          <NavLink to="/profil">Profil</NavLink>
        </nav>
        <div className="who">
          <span className="muted">{user.displayName}</span>
          <button className="secondary" onClick={onLogout}>Se déconnecter</button>
        </div>
      </header>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
