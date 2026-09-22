import { useState } from 'react'
import { Link, Navigate } from 'react-router'
import { useAuth } from '../auth/authContext'
import { TextField } from '../components/TextField'
import { useSubmit } from '../components/useSubmit'
import { AuthLayout } from './AuthLayout'

export function LoginPage() {
  const { state, login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const { onSubmit, pending, error } = useSubmit(() => login({ email, password }))

  if (state.status === 'authenticated') return <Navigate to="/" replace />

  return (
    <AuthLayout title="Connexion">
      <form onSubmit={onSubmit} className="stack">
        {error && <p role="alert" className="alert">{error}</p>}
        <TextField label="Email" type="email" autoComplete="email" required
                   value={email} onChange={(e) => setEmail(e.target.value)} />
        <TextField label="Mot de passe" type="password" autoComplete="current-password" required
                   value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit" disabled={pending}>Se connecter</button>
      </form>
      <p className="muted">
        Pas encore de compte ? <Link to="/inscription">Créer un compte</Link>
      </p>
    </AuthLayout>
  )
}
