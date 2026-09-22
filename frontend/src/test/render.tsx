import { render } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import { AppRoutes } from '../AppRoutes'
import { AuthProvider } from '../auth/AuthContext'

/** Monte l'application ENTIÈRE à une adresse donnée, comme si l'utilisateur l'ouvrait. */
export function renderApp(path = '/') {
  const user = userEvent.setup()
  render(
    <MemoryRouter initialEntries={[path]}>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </MemoryRouter>,
  )
  return { user }
}
