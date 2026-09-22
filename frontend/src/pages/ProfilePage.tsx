import { useState } from 'react'
import { profileApi } from '../api/endpoints'
import { useAuth, useCurrentUser } from '../auth/authContext'
import { TextField } from '../components/TextField'
import { useSubmit } from '../components/useSubmit'
import { parseDecimal } from '../domain/format'

export function ProfilePage() {
  const user = useCurrentUser()
  const { replaceUser } = useAuth()
  const [displayName, setDisplayName] = useState(user.displayName)
  const [weight, setWeight] = useState(String(user.weightKg).replace('.', ','))
  const [goal, setGoal] = useState(String(user.weeklyGoalMinutes))
  const [saved, setSaved] = useState(false)

  const { onSubmit, pending, error, fieldErrors } = useSubmit(async () => {
    setSaved(false)
    const updated = await profileApi.update({
      displayName,
      weightKg: parseDecimal(weight) ?? 0,
      weeklyGoalMinutes: Number(goal),
    })
    replaceUser(updated)
    setSaved(true)
  })

  return (
    <>
      <h1>Mon profil</h1>
      <form onSubmit={onSubmit} className="card stack profile" noValidate>
        {error && <p role="alert" className="alert">{error}</p>}
        <TextField label="Nom" error={fieldErrors.displayName}
                   value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
        <TextField label="Poids (kg)" inputMode="decimal" error={fieldErrors.weightKg}
                   value={weight} onChange={(e) => setWeight(e.target.value)} />
        <p className="muted small">Le poids sert à estimer les calories des prochaines séances.</p>
        <TextField label="Objectif (minutes par semaine)" type="number" error={fieldErrors.weeklyGoalMinutes}
                   value={goal} onChange={(e) => setGoal(e.target.value)} />
        <p className="muted small">L’OMS recommande au moins 150 minutes d’activité modérée par semaine.</p>
        <div className="actions">
          {saved && <span role="status" className="saved">Profil enregistré</span>}
          <button type="submit" disabled={pending}>Enregistrer</button>
        </div>
      </form>
    </>
  )
}
