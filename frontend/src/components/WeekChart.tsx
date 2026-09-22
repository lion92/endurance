import { formatDuration } from '../domain/format'

const DAYS = ['lundi', 'mardi', 'mercredi', 'jeudi', 'vendredi', 'samedi', 'dimanche']
const WIDTH = 560
const HEIGHT = 200
const BAR = 44
const GAP = (WIDTH - DAYS.length * BAR) / (DAYS.length - 1)

/** Un graphique en barres en SVG pur : sept rectangles et une règle de trois, sans bibliothèque. */
export function WeekChart({ minutesPerDay }: { minutesPerDay: number[] }) {
  const max = Math.max(60, ...minutesPerDay)
  return (
    <section className="card">
      <h2>Minutes par jour</h2>
      <svg viewBox={`0 0 ${WIDTH} ${HEIGHT + 28}`} className="week-chart" role="img" aria-label="Minutes par jour">
        {minutesPerDay.map((minutes, day) => {
          const h = Math.round((minutes / max) * HEIGHT)
          const x = day * (BAR + GAP)
          return (
            <g key={DAYS[day]}>
              <rect x={x} y={0} width={BAR} height={HEIGHT} rx={8} className="bar-bg" />
              <rect
                x={x}
                y={HEIGHT - h}
                width={BAR}
                height={h}
                rx={8}
                className="bar"
                aria-label={`${DAYS[day]} : ${formatDuration(minutes)}`}
              />
              <text x={x + BAR / 2} y={HEIGHT + 20} textAnchor="middle" className="bar-day">
                {DAYS[day].slice(0, 3)}
              </text>
            </g>
          )
        })}
      </svg>
    </section>
  )
}
