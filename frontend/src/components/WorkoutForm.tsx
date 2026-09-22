import { useState } from 'react'
import { workoutApi } from '../api/endpoints'
import type { Sport, Workout, WorkoutDetails } from '../api/types'
import { todayIso } from '../domain/dates'
import { parseDecimal } from '../domain/format'
import { SPORTS } from '../domain/sports'
import { TextField } from './TextField'
import { useSubmit } from './useSubmit'

interface Props {
  editing: Workout | null
  onSaved: () => void
  onCancel: () => void
}

export function WorkoutForm({ editing, onSaved, onCancel }: Props) {
  const [sport, setSport] = useState<Sport>(editing?.sport ?? 'RUNNING')
  const [date, setDate] = useState(editing?.date ?? todayIso())
  const [duration, setDuration] = useState(String(editing?.durationMinutes ?? 45))
  const [distance, setDistance] = useState(editing?.distanceKm?.toString().replace('.', ',') ?? '')
  const [effort, setEffort] = useState(editing?.effort ?? 5)
  const [notes, setNotes] = useState(editing?.notes ?? '')

  const { onSubmit, pending, error, fieldErrors } = useSubmit(async () => {
    const details: WorkoutDetails = {
      sport,
      date,
      durationMinutes: Number(duration),
      distanceKm: parseDecimal(distance),
      effort,
      notes: notes.trim() || null,
    }
    await (editing ? workoutApi.update(editing.id, details) : workoutApi.create(details))
    onSaved()
  })

  return (
    // noValidate : les règles vivent sur le SERVEUR, et ses messages s'affichent sous chaque champ.
    // Une seule source de vérité, au lieu de bulles du navigateur qui diraient autre chose.
    <form onSubmit={onSubmit} className="card workout-form" aria-label="Séance" noValidate>
      <h2>{editing ? 'Modifier la séance' : 'Nouvelle séance'}</h2>
      {error && <p role="alert" className="alert">{error}</p>}
      <div className="grid-3">
        <div className="field">
          <label htmlFor="sport">Sport</label>
          <select id="sport" value={sport} onChange={(e) => setSport(e.target.value as Sport)}>
            {SPORTS.map((s) => (
              <option key={s.value} value={s.value}>{s.label}</option>
            ))}
          </select>
        </div>
        <TextField label="Date" type="date" max={todayIso()} required error={fieldErrors.date}
                   value={date} onChange={(e) => setDate(e.target.value)} />
        <TextField label="Durée (min)" type="number" min={1} max={600} required
                   error={fieldErrors.durationMinutes}
                   value={duration} onChange={(e) => setDuration(e.target.value)} />
        <TextField label="Distance (km)" inputMode="decimal" placeholder="facultative"
                   error={fieldErrors.distanceKm}
                   value={distance} onChange={(e) => setDistance(e.target.value)} />
        <div className="field">
          <label htmlFor="effort">Ressenti : {effort}/10</label>
          <input id="effort" type="range" min={1} max={10} value={effort}
                 onChange={(e) => setEffort(Number(e.target.value))} />
        </div>
        <TextField label="Notes" placeholder="Comment ça s’est passé ?" maxLength={500}
                   value={notes} onChange={(e) => setNotes(e.target.value)} />
      </div>
      <div className="actions">
        <button type="button" className="secondary" onClick={onCancel}>Annuler</button>
        <button type="submit" disabled={pending}>Enregistrer</button>
      </div>
    </form>
  )
}
