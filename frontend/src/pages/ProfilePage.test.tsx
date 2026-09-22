import { screen } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import type { User } from '../api/types'
import { lea, loggedInAs } from '../test/fixtures'
import { renderApp } from '../test/render'
import { server } from '../test/server'

describe('page Profil', () => {
  it('part des valeurs actuelles', async () => {
    server.use(loggedInAs(lea))
    renderApp('/profil')

    expect(await screen.findByLabelText('Nom')).toHaveValue('Léa')
    expect(screen.getByLabelText('Poids (kg)')).toHaveValue('62')
    expect(screen.getByLabelText('Objectif (minutes par semaine)')).toHaveValue(150)
  })

  it('enregistre, confirme, et met à jour le nom affiché partout', async () => {
    let sent: unknown = null
    server.use(
      loggedInAs(lea),
      http.put('/api/me', async ({ request }) => {
        sent = await request.json()
        return HttpResponse.json({ ...lea, ...(sent as Partial<User>) })
      }),
    )
    const { user } = renderApp('/profil')

    await user.clear(await screen.findByLabelText('Nom'))
    await user.type(screen.getByLabelText('Nom'), 'Léa M.')
    await user.clear(screen.getByLabelText('Poids (kg)'))
    await user.type(screen.getByLabelText('Poids (kg)'), '61,5')
    await user.click(screen.getByRole('button', { name: 'Enregistrer' }))

    expect(await screen.findByRole('status')).toHaveTextContent('Profil enregistré')
    expect(sent).toEqual({ displayName: 'Léa M.', weightKg: 61.5, weeklyGoalMinutes: 150 })
    expect(screen.getByRole('banner')).toHaveTextContent('Léa M.')
  })

  it('affiche les refus du serveur sous chaque champ', async () => {
    server.use(
      loggedInAs(lea),
      http.put('/api/me', () =>
        HttpResponse.json(
          { status: 400, detail: 'Certains champs sont invalides', errors: { weightKg: 'Entre 25 et 300 kg' } },
          { status: 400 },
        ),
      ),
    )
    const { user } = renderApp('/profil')

    await user.clear(await screen.findByLabelText('Poids (kg)'))
    await user.type(screen.getByLabelText('Poids (kg)'), '8')
    await user.click(screen.getByRole('button', { name: 'Enregistrer' }))

    expect(await screen.findByText('Entre 25 et 300 kg')).toBeInTheDocument()
  })
})
