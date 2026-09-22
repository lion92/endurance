import { describe, expect, it } from 'vitest'
import { formatDistance, formatDuration, parseDecimal } from './format'

describe('formatDuration', () => {
  it('affiche les minutes seules sous une heure', () => {
    expect(formatDuration(45)).toBe('45 min')
  })

  it('affiche les heures et les minutes sur deux chiffres', () => {
    expect(formatDuration(65)).toBe('1 h 05')
  })

  it('affiche une heure pile sans minutes', () => {
    expect(formatDuration(120)).toBe('2 h')
  })
})

describe('formatDistance', () => {
  it('utilise la virgule décimale française', () => {
    expect(formatDistance(10.5)).toBe('10,5 km')
  })

  it("n'affiche rien quand la distance est inconnue", () => {
    expect(formatDistance(null)).toBe('—')
  })
})

describe('parseDecimal', () => {
  it('accepte la virgule comme le point', () => {
    expect(parseDecimal('42,5')).toBe(42.5)
    expect(parseDecimal('42.5')).toBe(42.5)
  })

  it('rend null pour un champ vide', () => {
    expect(parseDecimal('  ')).toBeNull()
  })
})
