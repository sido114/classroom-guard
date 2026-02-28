package org.acme

/**
 * Request DTO for creating a new classroom.
 * 
 * @property name The classroom name (required, max 100 characters)
 * @property description Optional description of the classroom (max 500 characters)
 */
data class CreateClassroomRequest(
    val name: String,
    val description: String? = null
)

/**
 * Request DTO for adding a URL to a classroom.
 * 
 * @property url The URL to add (required, will be validated and normalized)
 */
data class AddUrlRequest(
    val url: String
)

/**
 * Response DTO for classroom list view.
 * Contains summary information about a classroom.
 * 
 * @property id Unique classroom identifier
 * @property name Classroom name
 * @property description Optional classroom description
 * @property urlCount Number of URLs associated with this classroom
 * @property createdAt ISO-8601 formatted creation timestamp
 */
data class ClassroomListResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val urlCount: Long,
    val createdAt: String
)

/**
 * Response DTO for classroom detail view.
 * Contains full classroom information including all URLs.
 * 
 * @property id Unique classroom identifier
 * @property name Classroom name
 * @property description Optional classroom description
 * @property urls List of all URLs associated with this classroom
 * @property createdAt ISO-8601 formatted creation timestamp
 */
data class ClassroomDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val urls: List<UrlResponse>,
    val createdAt: String
)

/**
 * Response DTO for URL information.
 * 
 * @property id Unique URL identifier
 * @property url The normalized URL string
 * @property urlType Type of URL (e.g., "whitelist" or "blacklist")
 * @property createdAt ISO-8601 formatted creation timestamp
 */
data class UrlResponse(
    val id: Long,
    val url: String,
    val urlType: String,
    val createdAt: String
)
