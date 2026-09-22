import { useState, type FormEvent } from 'react'
import { ApiError } from '../api/http'

/**
 * La mécanique commune à tous les formulaires : empêcher le rechargement, désactiver le bouton
 * pendant l'envoi, et ranger l'erreur du serveur (message global + détail par champ).
 */
export function useSubmit(action: () => Promise<void>) {
  const [pending, setPending] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setPending(true)
    setError(null)
    setFieldErrors({})
    try {
      await action()
    } catch (e) {
      if (!(e instanceof ApiError)) throw e
      setFieldErrors(e.fieldErrors)
      if (Object.keys(e.fieldErrors).length === 0) setError(e.message)
    } finally {
      setPending(false)
    }
  }

  return { onSubmit, pending, error, fieldErrors }
}
