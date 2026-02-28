'use client'

import ClassroomCard from '@/components/ClassroomCard'
import { fetchClassrooms } from '@/lib/api/classrooms'
import type { Classroom } from '@/types/classroom'
import { useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'

export default function ClassroomsPage() {
  const [classrooms, setClassrooms] = useState<Classroom[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  useEffect(() => {
    loadClassrooms()
  }, [])

  const loadClassrooms = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await fetchClassrooms()
      setClassrooms(data)
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Unable to load classrooms. Please try again.'
      setError(message)
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleCreateClick = () => {
    router.push('/classrooms/new')
  }

  const handleClassroomClick = (id: number) => {
    router.push(`/classrooms/${id}`)
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-700">Loading classrooms...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-red-600">{error}</p>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">My Classrooms</h1>
          <button
            onClick={handleCreateClick}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors"
          >
            Create Classroom
          </button>
        </div>

        {classrooms.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-700 mb-4">
              You haven't created any classrooms yet.
            </p>
            <p className="text-gray-600 text-sm">
              Click "Create Classroom" to get started managing your class URLs.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {classrooms.map((classroom) => (
              <ClassroomCard
                key={classroom.id}
                classroom={classroom}
                onClick={() => handleClassroomClick(classroom.id)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
