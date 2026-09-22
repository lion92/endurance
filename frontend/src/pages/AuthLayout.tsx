import type { ReactNode } from 'react'

export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <main className="auth">
      <div className="auth-brand">
        <span className="logo">Endurance</span>
        <p>Chaque séance compte. Suivez-les toutes.</p>
      </div>
      <section className="card auth-card">
        <h1>{title}</h1>
        {children}
      </section>
    </main>
  )
}
