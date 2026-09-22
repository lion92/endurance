import { Link } from 'react-router'
import { statsApi } from '../api/endpoints'
import { useCurrentUser } from '../auth/authContext'
import { GoalProgress } from '../components/GoalProgress'
import { SportBreakdown } from '../components/SportBreakdown'
import { StatCard } from '../components/StatCard'
import { useLoad } from '../components/useLoad'
import { WeekChart } from '../components/WeekChart'
import { formatDay } from '../domain/dates'
import { formatDistance, formatDuration } from '../domain/format'
import { streakText } from '../domain/streak'

const integer = new Intl.NumberFormat('fr-FR')

export function DashboardPage() {
  const user = useCurrentUser()
  const { data: week, error } = useLoad(() => statsApi.week(), [])

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Bonjour {user.displayName}</h1>
          {week && (
            <p className="muted">Semaine du {formatDay(week.weekStart)} au {formatDay(week.weekEnd)}</p>
          )}
        </div>
        {week && (
          <span className="streak"><span aria-hidden="true">🔥 </span>{streakText(week.streakDays)}</span>
        )}
      </div>

      {error && <p role="alert" className="alert">{error}</p>}
      {week && week.sessions === 0 && (
        <p className="card empty">
          Aucune séance cette semaine — la première est la plus importante.{' '}
          <Link to="/seances">Enregistrer une séance</Link>
        </p>
      )}
      {week && (
        <>
          <div className="stats">
            <StatCard label="Séances" value={String(week.sessions)} />
            <StatCard label="Temps" value={formatDuration(week.totalMinutes)} />
            <StatCard label="Distance" value={formatDistance(week.totalDistanceKm)} />
            <StatCard label="Calories" value={`${integer.format(week.totalCalories)} kcal`} />
          </div>
          <GoalProgress minutes={week.totalMinutes} goalMinutes={week.goalMinutes} percent={week.goalPercent} />
          <div className="two-cols">
            <WeekChart minutesPerDay={week.minutesPerDay} />
            {week.minutesPerSport.length > 0 && <SportBreakdown minutesPerSport={week.minutesPerSport} />}
          </div>
        </>
      )}
    </>
  )
}
