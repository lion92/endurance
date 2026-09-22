const dayMonth = new Intl.DateTimeFormat('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' })

/** « Aujourd'hui » au format AAAA-MM-JJ, dans le fuseau du navigateur (pas en UTC). */
export function todayIso(): string {
  const now = new Date()
  return new Date(now.getTime() - now.getTimezoneOffset() * 60_000).toISOString().slice(0, 10)
}

export function formatDay(iso: string): string {
  const [year, month, day] = iso.split('-').map(Number)
  return dayMonth.format(new Date(year, month - 1, day))
}
