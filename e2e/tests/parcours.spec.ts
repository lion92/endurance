import { expect, test, type Page } from '@playwright/test'

const PASSWORD = 'un-mot-de-passe-solide'

async function signUp(page: Page, name: string) {
  const slug = name.normalize('NFD').replace(/[^a-z]/gi, '').toLowerCase()
  const email = `${slug}-${Date.now()}-${Math.random().toString(36).slice(2)}@endurance.test`
  await page.goto('/inscription')
  await page.getByLabel('Nom').fill(name)
  await page.getByLabel('Email').fill(email)
  await page.getByLabel('Mot de passe').fill(PASSWORD)
  await page.getByRole('button', { name: 'Créer mon compte' }).click()
  await expect(page.getByRole('heading', { name: `Bonjour ${name}` })).toBeVisible()
  return email
}

async function logWorkout(page: Page, sport: string, minutes: string, km: string) {
  await page.getByRole('link', { name: 'Séances' }).click()
  await page.getByRole('button', { name: 'Nouvelle séance' }).click()
  await page.getByLabel('Sport').selectOption({ label: sport })
  await page.getByLabel('Durée (min)').fill(minutes)
  await page.getByLabel('Distance (km)').fill(km)
  await page.getByRole('button', { name: 'Enregistrer' }).click()
  await expect(page.getByRole('row', { name: new RegExp(sport) })).toBeVisible()
}

test("le parcours complet d'une athlète, de l'inscription à la déconnexion", async ({ page }) => {
  const email = await signUp(page, 'Léa')

  await page.getByRole('link', { name: 'Profil' }).click()
  await page.getByLabel('Poids (kg)').fill('60')
  await page.getByRole('button', { name: 'Enregistrer' }).click()
  await expect(page.getByRole('status')).toHaveText('Profil enregistré')

  await logWorkout(page, 'Course', '60', '10,5')
  // 8,0 MET × 60 kg × 1 h : le calcul du SERVEUR, affiché par le site
  await expect(page.getByRole('row', { name: /Course/ })).toContainText('480 kcal')

  await page.getByRole('link', { name: 'Tableau de bord' }).click()
  await expect(page.getByRole('group', { name: 'Séances' })).toContainText('1')
  await expect(page.getByRole('group', { name: 'Distance' })).toContainText('10,5 km')
  await expect(page.getByText('60 / 150 min')).toBeVisible()

  await page.getByRole('button', { name: 'Se déconnecter' }).click()
  await page.goto('/seances')
  await expect(page.getByRole('heading', { name: 'Connexion' })).toBeVisible()

  await page.getByLabel('Email').fill(email.toUpperCase())
  await page.getByLabel('Mot de passe').fill(PASSWORD)
  await page.getByRole('button', { name: 'Se connecter' }).click()
  await expect(page.getByRole('heading', { name: 'Bonjour Léa' })).toBeVisible()
})

test('deux athlètes ne voient jamais les séances l’une de l’autre', async ({ browser }) => {
  const lea = await (await browser.newContext()).newPage()
  const tom = await (await browser.newContext()).newPage()
  await signUp(lea, 'Léa')
  await signUp(tom, 'Tom')

  await logWorkout(lea, 'Natation', '40', '1,8')
  await tom.getByRole('link', { name: 'Séances' }).click()

  await expect(tom.getByText('Aucune séance pour l’instant.')).toBeVisible()
})
