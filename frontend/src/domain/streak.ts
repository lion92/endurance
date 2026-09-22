export function streakText(days: number): string {
  if (days === 0) return 'Pas de série en cours'
  return days === 1 ? '1 jour d’affilée' : `${days} jours d’affilée`
}
