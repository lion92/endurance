import { useState } from 'react'
import { workoutApi } from '../api/endpoints'
import type { Workout } from '../api/types'
import { useLoad } from '../components/useLoad'
import { WorkoutForm } from '../components/WorkoutForm'
import { WorkoutTable } from '../components/WorkoutTable'

const PAGE_SIZE = 10

type Editing = { mode: 'closed' } | { mode: 'new' } | { mode: 'edit'; workout: Workout }

export function WorkoutsPage() {
  const [page, setPage] = useState(0)
  const [editing, setEditing] = useState<Editing>({ mode: 'closed' })
  const { data, error, reload } = useLoad(() => workoutApi.list(page, PAGE_SIZE), [page])

  function onSaved() {
    setEditing({ mode: 'closed' })
    reload()
  }

  async function onDelete(workout: Workout) {
    await workoutApi.remove(workout.id)
    reload()
  }

  return (
    <>
      <div className="page-head">
        <h1>Mes séances</h1>
        {editing.mode === 'closed' && (
          <button onClick={() => setEditing({ mode: 'new' })}>Nouvelle séance</button>
        )}
      </div>

      {editing.mode !== 'closed' && (
        <WorkoutForm
          key={editing.mode === 'edit' ? editing.workout.id : 'new'}
          editing={editing.mode === 'edit' ? editing.workout : null}
          onSaved={onSaved}
          onCancel={() => setEditing({ mode: 'closed' })}
        />
      )}

      {error && <p role="alert" className="alert">{error}</p>}
      {data && data.totalItems === 0 && (
        <p className="card empty">Aucune séance pour l’instant.</p>
      )}
      {data && data.totalItems > 0 && (
        <section className="card">
          <WorkoutTable
            workouts={data.items}
            onEdit={(workout) => setEditing({ mode: 'edit', workout })}
            onDelete={onDelete}
          />
          {data.totalPages > 1 && (
            <nav className="pager" aria-label="Pagination">
              <button className="secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>
                Page précédente
              </button>
              <span>Page {page + 1} sur {data.totalPages}</span>
              <button className="secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>
                Page suivante
              </button>
            </nav>
          )}
        </section>
      )}
    </>
  )
}
