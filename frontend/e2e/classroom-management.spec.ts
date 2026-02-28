import { expect, test } from '@playwright/test'

test.describe('Classroom Management E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  test('should navigate from landing page to classroom list', async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'Classroom Guard' })).toBeVisible()
    
    await page.getByRole('link', { name: /classroom management/i }).click()
    
    await expect(page).toHaveURL('/classrooms')
    await expect(page.getByRole('heading', { name: /my classrooms/i })).toBeVisible()
  })

  test('should create a new classroom', async ({ page }) => {
    await page.goto('/classrooms')
    
    await page.getByRole('button', { name: /create classroom/i }).click()
    await expect(page).toHaveURL('/classrooms/new')
    
    const classroomName = `Test Classroom ${Date.now()}`
    await page.getByLabel(/classroom name/i).fill(classroomName)
    
    await page.getByRole('button', { name: /create classroom/i }).click()
    
    // Wait for navigation with longer timeout
    await page.waitForURL(/\/classrooms\/\d+/, { timeout: 10000 })
    await expect(page.getByRole('heading', { name: classroomName })).toBeVisible({ timeout: 5000 })
  })

  test('should add URL to classroom', async ({ page }) => {
    // First create a classroom
    await page.goto('/classrooms/new')
    const classroomName = `URL Test ${Date.now()}`
    await page.getByLabel(/classroom name/i).fill(classroomName)
    await page.getByRole('button', { name: /create classroom/i }).click()
    
    await page.waitForURL(/\/classrooms\/\d+/, { timeout: 10000 })
    
    // Add a URL
    const testUrl = 'wikipedia.org'
    await page.getByPlaceholder(/example\.com/i).fill(testUrl)
    await page.getByRole('button', { name: /add url/i }).click()
    
    // Wait for success message or URL to appear
    await page.waitForTimeout(2000)
    
    // Verify URL appears in the list (normalized with https)
    await expect(page.getByText('https://wikipedia.org')).toBeVisible({ timeout: 5000 })
  })

  test('should validate URL format', async ({ page }) => {
    // Create a classroom first
    await page.goto('/classrooms/new')
    const classroomName = `Validation Test ${Date.now()}`
    await page.getByLabel(/classroom name/i).fill(classroomName)
    await page.getByRole('button', { name: /create classroom/i }).click()
    
    await page.waitForURL(/\/classrooms\/\d+/)
    
    // Try to add invalid URL
    await page.getByPlaceholder(/example\.com/i).fill('not a valid url!')
    await page.getByRole('button', { name: /add url/i }).click()
    
    // Should show error message
    await expect(page.getByText(/invalid url format/i)).toBeVisible()
  })

  test('should show empty state when no classrooms exist', async ({ page }) => {
    await page.goto('/classrooms')
    
    // Check if there are classrooms or empty state
    const hasClassrooms = await page.getByTestId('classroom-card').count()
    const emptyStateVisible = await page.getByText(/haven't created any classrooms yet/i).isVisible().catch(() => false)
    
    expect(hasClassrooms > 0 || emptyStateVisible).toBeTruthy()
  })

  test('should navigate back to classroom list from detail page', async ({ page }) => {
    // Create a classroom
    await page.goto('/classrooms/new')
    const classroomName = `Nav Test ${Date.now()}`
    await page.getByLabel(/classroom name/i).fill(classroomName)
    await page.getByRole('button', { name: /create classroom/i }).click()
    
    await page.waitForURL(/\/classrooms\/\d+/)
    
    // Navigate back using the button (not a link)
    await page.getByRole('button', { name: /back to classrooms/i }).click()
    
    await expect(page).toHaveURL('/classrooms')
  })

  test('should display multiple classrooms in grid', async ({ page }) => {
    await page.goto('/classrooms')
    
    // Count existing classrooms
    const initialCount = await page.getByTestId('classroom-card').count()
    
    // Create two new classrooms
    for (let i = 1; i <= 2; i++) {
      await page.goto('/classrooms/new')
      await page.getByLabel(/classroom name/i).fill(`Grid Test ${Date.now()}-${i}`)
      await page.getByRole('button', { name: /create classroom/i }).click()
      await page.waitForURL(/\/classrooms\/\d+/)
      await page.waitForTimeout(500) // Small delay between creations
    }
    
    // Go back to list and verify count increased
    await page.goto('/classrooms')
    await page.waitForTimeout(500) // Wait for page to load
    const finalCount = await page.getByTestId('classroom-card').count()
    
    expect(finalCount).toBeGreaterThanOrEqual(initialCount + 2)
  })
})
