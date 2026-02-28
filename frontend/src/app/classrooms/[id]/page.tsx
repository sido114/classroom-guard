'use client'

import { addUrlToClassroom, fetchClassroomDetail } from '@/lib/api/classrooms'
import type { ClassroomDetail } from '@/types/classroom'
import { useRouter } from 'next/navigation'
import { use, useEffect, useState } from 'react'

export default function ClassroomDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const [classroom, setClassroom] = useState<ClassroomDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [urlInput, setUrlInput] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [validationError, setValidationError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const router = useRouter()

  const { id } = use(params)
  const classroomId = parseInt(id, 10)

  useEffect(() => {
    loadClassroom()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [classroomId])

  const loadClassroom = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await fetchClassroomDetail(classroomId)
      setClassroom(data)
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Unable to load classroom. Please try again.'
      setError(message)
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleAddUrl = async (e: React.FormEvent) => {
    e.preventDefault()
    setValidationError(null)
    setSuccessMessage(null)

    if (!urlInput.trim()) {
      setValidationError('URL cannot be empty')
      return
    }

    try {
      setSubmitting(true)
      const newUrl = await addUrlToClassroom(classroomId, { url: urlInput })
      
      // Update classroom with new URL
      if (classroom) {
        setClassroom({
          ...classroom,
          urls: [...classroom.urls, newUrl]
        })
      }
      
      setUrlInput('')
      setSuccessMessage('URL added successfully')
      setTimeout(() => setSuccessMessage(null), 3000)
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Unable to add URL. Please try again.'
      setValidationError(message)
      console.error(err)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-700">Loading classroom...</p>
      </div>
    )
  }

  if (error || !classroom) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error || 'Classroom not found'}</p>
          <button
            onClick={() => router.push('/classrooms')}
            className="text-blue-600 hover:text-blue-800"
          >
            Back to Classrooms
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-4xl mx-auto">
        <button
          onClick={() => router.push('/classrooms')}
          className="mb-6 text-blue-600 hover:text-blue-800 flex items-center gap-2"
        >
          ← Back to Classrooms
        </button>

        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <h1 className="text-3xl font-bold mb-2 text-gray-900">{classroom.name}</h1>
          {classroom.description && (
            <p className="text-gray-700">{classroom.description}</p>
          )}
        </div>

        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <h2 className="text-xl font-semibold mb-4 text-gray-900">Add URL</h2>
          <form onSubmit={handleAddUrl} className="space-y-4">
            <div>
              <input
                type="text"
                value={urlInput}
                onChange={(e) => setUrlInput(e.target.value)}
                placeholder="example.com or https://example.com"
                disabled={submitting}
                className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-500"
              />
              {validationError && (
                <p className="text-red-600 text-sm mt-1">{validationError}</p>
              )}
              {successMessage && (
                <p className="text-green-600 text-sm mt-1">{successMessage}</p>
              )}
            </div>
            <button
              type="submit"
              disabled={submitting}
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors disabled:bg-gray-400"
            >
              {submitting ? 'Adding...' : 'Add URL'}
            </button>
          </form>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4 text-gray-900">URLs ({classroom.urls.length})</h2>
          {classroom.urls.length === 0 ? (
            <p className="text-gray-700 text-center py-8">
              No URLs added yet. Add your first URL above.
            </p>
          ) : (
            <ul className="space-y-3">
              {classroom.urls.map((urlItem) => (
                <li
                  key={urlItem.id}
                  className="border-b border-gray-200 pb-3 last:border-b-0"
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <p className="text-blue-600 break-all">{urlItem.url}</p>
                      <p className="text-gray-600 text-sm mt-1">
                        Added: {new Date(urlItem.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  )
}
