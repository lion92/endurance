/** Une erreur renvoyée par l'API, au format « Problem Details » (RFC 9457). */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: Record<string, string>

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE'

async function request<T>(method: Method, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (method !== 'GET') {
    // Spring Security (csrf.spa) dépose ce cookie LISIBLE ; le renvoyer en en-tête prouve que
    // la requête vient bien de NOTRE page : un autre site ne peut pas lire nos cookies.
    const csrf = readCookie('XSRF-TOKEN')
    if (csrf) headers['X-XSRF-TOKEN'] = csrf
  }

  const response = await fetch(new URL(path, window.location.origin), {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
    credentials: 'same-origin',
  })

  if (!response.ok) throw await toApiError(response)
  if (response.status === 204) return undefined as T
  return (await response.json()) as T
}

async function toApiError(response: Response): Promise<ApiError> {
  const problem = await response.json().catch(() => null)
  if (problem && typeof problem.detail === 'string') {
    return new ApiError(response.status, problem.detail, problem.errors ?? {})
  }
  return new ApiError(response.status, `Le serveur ne répond pas correctement (${response.status})`)
}

function readCookie(name: string): string | null {
  const match = document.cookie.split('; ').find((cookie) => cookie.startsWith(`${name}=`))
  return match ? decodeURIComponent(match.slice(name.length + 1)) : null
}

export const http = {
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
  delete: <T = void>(path: string) => request<T>('DELETE', path),
}
