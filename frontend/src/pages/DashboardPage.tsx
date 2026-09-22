import { useCurrentUser } from '../auth/authContext'

export function DashboardPage() {
  const user = useCurrentUser()
  return <h1>Bonjour {user.displayName}</h1>
}
