'use client'
import { useEffect, useState } from 'react'

interface SavedUrl {
  id: number
  url: string
  createdAt: string
}

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export default function Home() {
  const [urls, setUrls] = useState<SavedUrl[]>([])
  const [inputUrl, setInputUrl] = useState('')
  const [loading, setLoading] = useState(false)

  const fetchUrls = async () => {
    try {
      const res = await fetch(`${API_URL}/api/urls`)
      if (res.ok) {
        const data = await res.json()
        setUrls(data)
      }
    } catch (error) {
      console.error('Failed to fetch URLs:', error)
    }
  }

  useEffect(() => {
    fetchUrls()
  }, [])

  const handleSaveUrl = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!inputUrl.trim()) return

    setLoading(true)
    try {
      const res = await fetch(`${API_URL}/api/urls`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url: inputUrl })
      })

      if (res.ok) {
        setInputUrl('')
        await fetchUrls()
      }
    } catch (error) {
      console.error('Failed to save URL:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="p-24">
      <h1 className="text-4xl font-bold mb-8">URL Dashboard</h1>

      <form onSubmit={handleSaveUrl} className="mb-8">
        <div className="flex gap-4">
          <input
            type="text"
            value={inputUrl}
            onChange={(e) => setInputUrl(e.target.value)}
            placeholder="Enter URL..."
            className="flex-1 px-4 py-2 border rounded"
            disabled={loading}
          />
          <button
            type="submit"
            disabled={loading || !inputUrl.trim()}
            className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:bg-gray-400"
          >
            {loading ? 'Saving...' : 'Save URL'}
          </button>
        </div>
      </form>

      <div>
        <h2 className="text-2xl font-semibold mb-4">Saved URLs</h2>
        {urls.length === 0 ? (
          <p className="text-gray-500">No URLs saved yet</p>
        ) : (
          <ul className="space-y-2">
            {urls.map((item) => (
              <li key={item.id} className="p-4 border rounded">
                <a
                  href={item.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:underline"
                >
                  {item.url}
                </a>
                <p className="text-sm text-gray-500 mt-1">
                  Saved: {new Date(item.createdAt).toLocaleString()}
                </p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </main>
  )
}
