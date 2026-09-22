import { useState } from 'react'
import type { Workout } from '../api/types'
import { formatDay } from '../domain/dates'
import { formatDistance, formatDuration } from '../domain/format'
import { sportColor, sportLabel } from '../domain/sports'

interface Props {
  workouts: Workout[]
  onEdit: (workout: Workout) => void
  onDelete: (workout: Workout) => void
}

export function WorkoutTable({ workouts, onEdit, onDelete }: Props) {
  const [confirming, setConfirming] = useState<number | null>(null)

  return (
    <table className="workouts">
      <thead>
        <tr>
          <th>Sport</th><th>Date</th><th>Durée</th><th>Distance</th><th>Ressenti</th><th>Calories</th><th />
        </tr>
      </thead>
      <tbody>
        {workouts.map((w) => (
          <tr key={w.id} aria-label={`${sportLabel(w.sport)} du ${formatDay(w.date)}`}>
            <td>
              <span className="sport-dot" style={{ background: sportColor(w.sport) }} />
              <strong>{sportLabel(w.sport)}</strong>
              {w.notes && <div className="muted small">{w.notes}</div>}
            </td>
            <td>{formatDay(w.date)}</td>
            <td>{formatDuration(w.durationMinutes)}</td>
            <td>{formatDistance(w.distanceKm)}</td>
            <td>{w.effort}/10</td>
            <td>{w.calories} kcal</td>
            <td className="row-actions">
              {confirming === w.id ? (
                <>
                  <button className="danger" onClick={() => onDelete(w)}>Confirmer</button>
                  <button className="secondary" onClick={() => setConfirming(null)}>Annuler</button>
                </>
              ) : (
                <>
                  <button className="secondary" onClick={() => onEdit(w)}>Modifier</button>
                  <button className="danger" onClick={() => setConfirming(w.id)}>Supprimer</button>
                </>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
