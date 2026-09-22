import { http as mock, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '../test/server'
import { ApiError, http } from './http'

describe('client HTTP', () => {
  it("renvoie le jeton CSRF lu dans le cookie, dans l'en-tête X-XSRF-TOKEN", async () => {
    document.cookie = 'XSRF-TOKEN=jeton-123'
    let header: string | null = null
    server.use(
      mock.post('/api/workouts', ({ request }) => {
        header = request.headers.get('X-XSRF-TOKEN')
        return HttpResponse.json({ id: 1 }, { status: 201 })
      }),
    )

    await http.post('/api/workouts', { sport: 'RUNNING' })

    expect(header).toBe('jeton-123')
  })

  it('envoie et reçoit du JSON', async () => {
    server.use(
      mock.post('/api/echo', async ({ request }) => HttpResponse.json(await request.json())),
    )

    await expect(http.post('/api/echo', { a: 1 })).resolves.toEqual({ a: 1 })
  })

  it('ne cherche pas de corps dans une réponse 204', async () => {
    server.use(mock.delete('/api/workouts/1', () => new HttpResponse(null, { status: 204 })))

    await expect(http.delete('/api/workouts/1')).resolves.toBeUndefined()
  })

  it("transforme une erreur « Problem Details » en ApiError, champ par champ", async () => {
    server.use(
      mock.post('/api/auth/register', () =>
        HttpResponse.json(
          { status: 400, detail: 'Certains champs sont invalides', errors: { password: '12 caractères minimum' } },
          { status: 400 },
        ),
      ),
    )

    const error = await http.post('/api/auth/register', {}).catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error).toMatchObject({
      status: 400,
      message: 'Certains champs sont invalides',
      fieldErrors: { password: '12 caractères minimum' },
    })
  })

  it("donne un message lisible quand le serveur ne répond pas en JSON", async () => {
    server.use(mock.get('/api/me', () => new HttpResponse('Bad Gateway', { status: 502 })))

    await expect(http.get('/api/me')).rejects.toMatchObject({
      status: 502,
      message: 'Le serveur ne répond pas correctement (502)',
    })
  })
})
