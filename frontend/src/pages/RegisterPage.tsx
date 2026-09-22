import { useState } from 'react'
import { Link, Navigate } from 'react-router'
import { useAuth } from '../auth/authContext'
import { TextField } from '../components/TextField'
import { useSubmit } from '../components/useSubmit'
import { AuthLayout } from './AuthLayout'

export function RegisterPage() {
  const { state, register } = useAuth()
  const [displayName, setDisplayName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const { onSubmit, pending, error, fieldErrors } = useSubmit(() =>
    register({ displayName, email, password }),
  )

  if (state.status === 'authenticated') return <Navigate to="/" replace />

  return (
    <AuthLayout title="Créer un compte">
      <form onSubmit={onSubmit} className="stack">
        {error && <p role="alert" className="alert">{error}</p>}
        <TextField label="Nom" autoComplete="nickname" required error={fieldErrors.displayName}
                   value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
        <TextField label="Email" type="email" autoComplete="email" required error={fieldErrors.email}
                   value={email} onChange={(e) => setEmail(e.target.value)} />
        <TextField label="Mot de passe" type="password" autoComplete="new-password" required
                   error={fieldErrors.password} placeholder="12 caractères minimum"
                   value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit" disabled={pending}>Créer mon compte</button>
      </form>
      <p className="muted">
        Déjà inscrit ? <Link to="/connexion">Se connecter</Link>
      </p>
    </AuthLayout>
  )
}
