import { useCallback, useEffect, useState } from 'react'

/**
 * Charge une donnée au montage et quand ses dépendances changent ; reload() la recharge.
 * Une réponse arrivée après un changement de page est ignorée (drapeau « active »).
 */
export function useLoad<T>(loader: () => Promise<T>, deps: unknown[]) {
  const [data, setData] = useState<T | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [version, setVersion] = useState(0)

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const load = useCallback(loader, deps)

  useEffect(() => {
    let active = true
    load()
      .then((value) => active && setData(value))
      .catch((e: Error) => active && setError(e.message))
    return () => {
      active = false
    }
  }, [load, version])

  return { data, error, reload: () => setVersion((v) => v + 1) }
}
