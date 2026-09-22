const decimal = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 2 })

export function formatDuration(minutes: number): string {
  if (minutes < 60) return `${minutes} min`
  const hours = Math.floor(minutes / 60)
  const rest = minutes % 60
  return rest === 0 ? `${hours} h` : `${hours} h ${String(rest).padStart(2, '0')}`
}

export function formatDistance(km: number | null): string {
  return km === null ? '—' : `${decimal.format(km)} km`
}
