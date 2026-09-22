import type { Sport } from '../api/types'

export const SPORTS: { value: Sport; label: string }[] = [
  { value: 'RUNNING', label: 'Course' },
  { value: 'CYCLING', label: 'Vélo' },
  { value: 'SWIMMING', label: 'Natation' },
  { value: 'STRENGTH', label: 'Musculation' },
  { value: 'WALKING', label: 'Marche' },
  { value: 'YOGA', label: 'Yoga' },
]

export function sportLabel(sport: Sport): string {
  return SPORTS.find((s) => s.value === sport)?.label ?? sport
}

/** La couleur d'un sport vit dans le CSS (--sport-running…) : un seul endroit pour la changer. */
export function sportColor(sport: Sport): string {
  return `var(--sport-${sport.toLowerCase()})`
}
