import type {
    AddUrlRequest,
    Classroom,
    ClassroomDetail,
    ClassroomUrl,
    CreateClassroomRequest,
} from '@/types/classroom'

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

/**
 * Parse error response from backend
 */
async function parseErrorMessage(response: Response): Promise<string> {
  try {
    const data = await response.json()
    // Backend returns { "error": "message" } format
    if (data.error) {
      return data.error
    }
    // Fallback to generic message
    return `Request failed with status ${response.status}`
  } catch {
    // If JSON parsing fails, return generic message
    return `Request failed with status ${response.status}`
  }
}

export async function fetchClassrooms(): Promise<Classroom[]> {
  const res = await fetch(`${API_URL}/api/classrooms`)
  if (!res.ok) {
    const errorMessage = await parseErrorMessage(res)
    throw new Error(errorMessage)
  }
  return res.json()
}

export async function fetchClassroomDetail(id: number): Promise<ClassroomDetail> {
  const res = await fetch(`${API_URL}/api/classrooms/${id}`)
  if (!res.ok) {
    if (res.status === 404) {
      throw new Error('Classroom not found')
    }
    const errorMessage = await parseErrorMessage(res)
    throw new Error(errorMessage)
  }
  return res.json()
}

export async function createClassroom(data: CreateClassroomRequest): Promise<Classroom> {
  const res = await fetch(`${API_URL}/api/classrooms`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    const errorMessage = await parseErrorMessage(res)
    throw new Error(errorMessage)
  }
  return res.json()
}

export async function addUrlToClassroom(
  classroomId: number,
  data: AddUrlRequest
): Promise<ClassroomUrl> {
  const res = await fetch(`${API_URL}/api/classrooms/${classroomId}/urls`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    const errorMessage = await parseErrorMessage(res)
    throw new Error(errorMessage)
  }
  return res.json()
}
