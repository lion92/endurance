import type { Sport } from '../api/types'
import { formatDuration } from '../domain/format'
import { sportColor, sportLabel } from '../domain/sports'

export function SportBreakdown({ minutesPerSport }: { minutesPerSport: { sport: Sport; minutes: number }[] }) {
  const total = minutesPerSport.reduce((sum, s) => sum + s.minutes, 0)
  return (
    <section className="card">
      <h2>Par sport</h2>
      <ul className="sports">
        {minutesPerSport.map(({ sport, minutes }) => (
          <li key={sport} aria-label={sportLabel(sport)}>
            <div className="sport-line">
              <span><span className="sport-dot" style={{ background: sportColor(sport) }} />{sportLabel(sport)}</span>
              <span className="muted">{formatDuration(minutes)}</span>
            </div>
            <div className="sport-track">
              <div style={{ width: `${(minutes / total) * 100}%`, background: sportColor(sport) }} />
            </div>
          </li>
        ))}
      </ul>
    </section>
  )
}
