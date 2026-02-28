'use client'

import { createClassroom } from '@/lib/api/classrooms'
import type { CreateClassroomRequest } from '@/types/classroom'
import { useState } from 'react'

interface CreateClassroomFormProps {
  onSuccess: () => void
  onCancel: () => void
}

export default function CreateClassroomForm({ onSuccess, onCancel }: CreateClassroomFormProps) {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [nameError, setNameError] = useState<string | null>(null)
  const [descriptionError, setDescriptionError] = useState<string | null>(null)

  const validateName = (value: string): boolean => {
    if (!value.trim()) {
      setNameError('Name is required')
      return false
    }
    if (value.length > 100) {
      setNameError('Name must be 100 characters or less')
      return false
    }
    setNameError(null)
    return true
  }

  const validateDescription = (value: string): boolean => {
    if (value.length > 500) {
      setDescriptionError('Description must be 500 characters or less')
      return false
    }
    setDescriptionError(null)
    return true
  }

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    setName(value)
    validateName(value)
  }

  const handleDescriptionChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const value = e.target.value
    setDescription(value)
    validateDescription(value)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    const isNameValid = validateName(name)
    const isDescriptionValid = validateDescription(description)

    if (!isNameValid || !isDescriptionValid) {
      return
    }

    try {
      setLoading(true)
      const request: CreateClassroomRequest = {
        name: name.trim(),
        description: description.trim() || undefined
      }
      await createClassroom(request)
      setName('')
      setDescription('')
      onSuccess()
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Unable to create classroom. Please try again.'
      setError(message)
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="bg-white border rounded-lg p-6 max-w-2xl">
      <h2 className="text-2xl font-bold mb-6">Create New Classroom</h2>

      {error && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded text-red-700 text-sm">
          {error}
        </div>
      )}

      <div className="mb-4">
        <label htmlFor="name" className="block text-sm font-medium mb-2 text-gray-900">
          Classroom Name <span className="text-red-500">*</span>
        </label>
        <input
          id="name"
          type="text"
          value={name}
          onChange={handleNameChange}
          disabled={loading}
          className={`w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-500 ${
            nameError ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="e.g., Math 101"
        />
        <div className="flex justify-between items-center mt-1">
          {nameError ? (
            <span className="text-red-500 text-sm">{nameError}</span>
          ) : (
            <span className="text-gray-600 text-sm">Required field</span>
          )}
          <span className={`text-sm ${name.length > 100 ? 'text-red-500' : 'text-gray-600'}`}>
            {name.length}/100
          </span>
        </div>
      </div>

      <div className="mb-6">
        <label htmlFor="description" className="block text-sm font-medium mb-2 text-gray-900">
          Description
        </label>
        <textarea
          id="description"
          value={description}
          onChange={handleDescriptionChange}
          disabled={loading}
          rows={4}
          className={`w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-500 ${
            descriptionError ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="Optional description for this classroom"
        />
        <div className="flex justify-between items-center mt-1">
          {descriptionError ? (
            <span className="text-red-500 text-sm">{descriptionError}</span>
          ) : (
            <span className="text-gray-600 text-sm">Optional</span>
          )}
          <span className={`text-sm ${description.length > 500 ? 'text-red-500' : 'text-gray-600'}`}>
            {description.length}/500
          </span>
        </div>
      </div>

      <div className="flex gap-3">
        <button
          type="submit"
          disabled={loading || !!nameError || !!descriptionError}
          className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          {loading ? 'Creating...' : 'Create Classroom'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          disabled={loading}
          className="bg-gray-200 text-gray-700 px-6 py-2 rounded hover:bg-gray-300 transition-colors disabled:bg-gray-100 disabled:cursor-not-allowed"
        >
          Cancel
        </button>
      </div>
    </form>
  )
}
