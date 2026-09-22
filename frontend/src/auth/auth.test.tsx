import { screen } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { lea, loggedInAs, notLoggedIn } from '../test/fixtures'
import { renderApp } from '../test/render'
import { server } from '../test/server'

describe('connexion', () => {
  it('renvoie un visiteur non connecté vers la page de connexion', async () => {
    server.use(notLoggedIn)

    renderApp('/')

    expect(await screen.findByRole('heading', { name: 'Connexion' })).toBeInTheDocument()
  })

  it('mène au tableau de bord une fois connecté', async () => {
    server.use(notLoggedIn, http.post('/api/auth/login', () => HttpResponse.json(lea)))
    const { user } = renderApp('/')

    await user.type(await screen.findByLabelText('Email'), 'lea@endurance.fr')
    await user.type(screen.getByLabelText('Mot de passe'), 'un-mot-de-passe-solide')
    await user.click(screen.getByRole('button', { name: 'Se connecter' }))

    expect(await screen.findByRole('heading', { name: 'Bonjour Léa' })).toBeInTheDocument()
  })

  it('affiche le message du serveur quand le mot de passe est faux', async () => {
    server.use(
      notLoggedIn,
      http.post('/api/auth/login', () =>
        HttpResponse.json({ status: 401, detail: 'Email ou mot de passe incorrect' }, { status: 401 }),
      ),
    )
    const { user } = renderApp('/connexion')

    await user.type(await screen.findByLabelText('Email'), 'lea@endurance.fr')
    await user.type(screen.getByLabelText('Mot de passe'), 'pas-le-bon')
    await user.click(screen.getByRole('button', { name: 'Se connecter' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('Email ou mot de passe incorrect')
  })

  it('reprend la session au rechargement de la page', async () => {
    server.use(loggedInAs(lea))

    renderApp('/')

    expect(await screen.findByRole('heading', { name: 'Bonjour Léa' })).toBeInTheDocument()
  })

  it('ramène à la page de connexion après la déconnexion', async () => {
    server.use(loggedInAs(lea), http.post('/api/auth/logout', () => new HttpResponse(null, { status: 204 })))
    const { user } = renderApp('/')

    await user.click(await screen.findByRole('button', { name: 'Se déconnecter' }))

    expect(await screen.findByRole('heading', { name: 'Connexion' })).toBeInTheDocument()
  })
})

describe('inscription', () => {
  it('affiche sous chaque champ l’erreur renvoyée par le serveur', async () => {
    server.use(
      notLoggedIn,
      http.post('/api/auth/register', () =>
        HttpResponse.json(
          { status: 400, detail: 'Certains champs sont invalides', errors: { password: '12 caractères minimum' } },
          { status: 400 },
        ),
      ),
    )
    const { user } = renderApp('/inscription')

    await user.type(await screen.findByLabelText('Nom'), 'Léa')
    await user.type(screen.getByLabelText('Email'), 'lea@endurance.fr')
    await user.type(screen.getByLabelText('Mot de passe'), 'court')
    await user.click(screen.getByRole('button', { name: 'Créer mon compte' }))

    expect(await screen.findByText('12 caractères minimum')).toBeInTheDocument()
    expect(screen.getByLabelText('Mot de passe')).toHaveAttribute('aria-invalid', 'true')
  })

  it('connecte directement après une inscription réussie', async () => {
    server.use(
      notLoggedIn,
      http.post('/api/auth/register', () => HttpResponse.json(lea, { status: 201 })),
      http.post('/api/auth/login', () => HttpResponse.json(lea)),
    )
    const { user } = renderApp('/inscription')

    await user.type(await screen.findByLabelText('Nom'), 'Léa')
    await user.type(screen.getByLabelText('Email'), 'lea@endurance.fr')
    await user.type(screen.getByLabelText('Mot de passe'), 'un-mot-de-passe-solide')
    await user.click(screen.getByRole('button', { name: 'Créer mon compte' }))

    expect(await screen.findByRole('heading', { name: 'Bonjour Léa' })).toBeInTheDocument()
  })
})
