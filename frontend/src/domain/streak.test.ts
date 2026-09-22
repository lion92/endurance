import { expect, it } from 'vitest'
import { streakText } from './streak'

it('accorde « jour » au singulier et au pluriel', () => {
  expect(streakText(1)).toBe('1 jour d’affilée')
  expect(streakText(2)).toBe('2 jours d’affilée')
})
