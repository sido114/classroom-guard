import { test as setup } from '@playwright/test'

// Global setup - runs once before all tests
setup('wait for backend to be ready', async ({ request }) => {
  const maxRetries = 30
  const retryDelay = 2000
  
  for (let i = 0; i < maxRetries; i++) {
    try {
      const response = await request.get('http://localhost:8080/q/health/ready')
      if (response.ok()) {
        console.log('Backend is ready!')
        return
      }
    } catch (error) {
      console.debug(error)
      console.log(`Waiting for backend... (${i + 1}/${maxRetries})`)
    }
    await new Promise(resolve => setTimeout(resolve, retryDelay))
  }
  
  throw new Error('Backend did not become ready in time')
})
