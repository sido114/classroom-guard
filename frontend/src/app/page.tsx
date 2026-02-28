'use client'
import { useEffect, useState } from 'react'

export default function Home() {
  const [data, setData] = useState<{message: string} | null>(null)

  useEffect(() => {
    fetch('http://localhost:8080/hello')
      .then(res => res.json())
      .then(setData)
  }, [])

  return (
    <main className="p-24">
      <h1 className="text-4xl font-bold">Classroom Guard</h1>
      <p className="mt-4 text-xl">
        Backend Status: {data ? data.message : 'Connecting...'}
      </p>
    </main>
  )
}