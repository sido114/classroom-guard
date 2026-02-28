'use client'

import CreateClassroomForm from '@/components/CreateClassroomForm'
import { useRouter } from 'next/navigation'

export default function NewClassroomPage() {
  const router = useRouter()

  const handleSuccess = () => {
    router.push('/classrooms')
  }

  const handleCancel = () => {
    router.push('/classrooms')
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-6xl mx-auto">
        <button
          onClick={handleCancel}
          className="mb-6 text-blue-600 hover:text-blue-800 flex items-center gap-2"
        >
          ← Back to Classrooms
        </button>
        <CreateClassroomForm onSuccess={handleSuccess} onCancel={handleCancel} />
      </div>
    </div>
  )
}
