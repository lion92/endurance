export function StatCard({ label, value }: { label: string; value: string }) {
  return (
    <div role="group" aria-label={label} className="card stat">
      <span className="stat-label">{label}</span>
      <strong className="stat-value">{value}</strong>
    </div>
  )
}
