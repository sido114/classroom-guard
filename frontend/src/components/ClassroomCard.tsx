import type { Classroom } from '@/types/classroom'

interface ClassroomCardProps {
  classroom: Classroom
  onClick: () => void
}

export default function ClassroomCard({ classroom, onClick }: ClassroomCardProps) {
  const truncateDescription = (text: string | undefined, maxLength: number) => {
    if (!text) return 'No description'
    if (text.length <= maxLength) return text
    return text.substring(0, maxLength) + '...'
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
  }

  return (
    <div
      onClick={onClick}
      className="border rounded-lg p-4 cursor-pointer hover:shadow-lg transition-shadow bg-white"
    >
      <h3 className="text-lg font-semibold mb-2 text-gray-900">{classroom.name}</h3>
      <p className="text-gray-700 text-sm mb-3">
        {truncateDescription(classroom.description, 100)}
      </p>
      <div className="flex justify-between items-center text-sm">
        <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded">
          {classroom.urlCount} {classroom.urlCount === 1 ? 'URL' : 'URLs'}
        </span>
        <span className="text-gray-600">
          {formatDate(classroom.createdAt)}
        </span>
      </div>
    </div>
  )
}
