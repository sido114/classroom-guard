'use client'
import Link from 'next/link'

export default function Home() {
  return (
    <main className="min-h-screen bg-gray-50 flex items-center justify-center p-8">
      <div className="max-w-4xl w-full">
        <div className="text-center mb-12">
          <h1 className="text-5xl font-bold mb-4 text-gray-900">Classroom Guard</h1>
          <p className="text-xl text-gray-700">
            Manage classroom internet access for Swiss schools
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          <Link
            href="/classrooms"
            className="bg-white border-2 border-blue-600 rounded-lg p-8 hover:shadow-lg transition-shadow cursor-pointer"
          >
            <h2 className="text-2xl font-semibold mb-3 text-blue-600">
              Classroom Management
            </h2>
            <p className="text-gray-700 mb-4">
              Create and manage classrooms with custom URL whitelists for controlled internet access.
            </p>
            <span className="text-blue-600 font-medium">
              Go to Classrooms →
            </span>
          </Link>

          <div className="bg-white border-2 border-gray-300 rounded-lg p-8 opacity-60">
            <h2 className="text-2xl font-semibold mb-3 text-gray-700">
              Session Control
            </h2>
            <p className="text-gray-700 mb-4">
              Start focus sessions and control student device access in real-time.
            </p>
            <span className="text-gray-500 font-medium">
              Coming Soon
            </span>
          </div>
        </div>

        <div className="mt-12 text-center text-gray-600 text-sm">
          <p>Swiss EdTech Solution • Phase 1: Classroom URL Management</p>
        </div>
      </div>
    </main>
  )
}
