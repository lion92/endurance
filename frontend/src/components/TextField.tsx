import { useId, type InputHTMLAttributes } from 'react'

interface Props extends InputHTMLAttributes<HTMLInputElement> {
  label: string
  error?: string
}

/**
 * Un champ, son libellé et son erreur, reliés pour les lecteurs d'écran.
 * L'erreur reste HORS du <label> : sinon elle s'ajouterait au nom accessible du champ.
 */
export function TextField({ label, error, ...input }: Props) {
  const id = useId()
  const errorId = `${id}-error`
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      <input id={id} aria-invalid={error ? true : undefined} aria-describedby={error ? errorId : undefined} {...input} />
      {error && (
        <span id={errorId} className="field-error">
          {error}
        </span>
      )}
    </div>
  )
}
