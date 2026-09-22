interface Props {
  minutes: number
  goalMinutes: number
  percent: number
}

export function GoalProgress({ minutes, goalMinutes, percent }: Props) {
  // Le serveur dit la vérité (120 %) ; la barre, elle, s'arrête au bord.
  const filled = Math.min(percent, 100)
  return (
    <section className="card goal">
      <div className="goal-head">
        <h2>Objectif de la semaine</h2>
        <span className="muted">{minutes} / {goalMinutes} min</span>
      </div>
      <div
        role="progressbar"
        aria-label="Objectif de la semaine"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={filled}
        className="goal-track"
      >
        <div className={percent >= 100 ? 'goal-fill done' : 'goal-fill'} style={{ width: `${filled}%` }} />
      </div>
      <p className="goal-note">{percent >= 100 ? 'Objectif atteint !' : `${percent} % — encore ${goalMinutes - minutes} min`}</p>
    </section>
  )
}
