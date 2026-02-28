// Data models for classroom management

export interface Classroom {
  id: number
  name: string
  description?: string
  urlCount: number
  createdAt: string
}

export interface ClassroomUrl {
  id: number
  url: string
  urlType: string
  createdAt: string
}

export interface ClassroomDetail {
  id: number
  name: string
  description?: string
  createdAt: string
  urls: ClassroomUrl[]
}

// Request models
export interface CreateClassroomRequest {
  name: string
  description?: string
}

export interface AddUrlRequest {
  url: string
}
