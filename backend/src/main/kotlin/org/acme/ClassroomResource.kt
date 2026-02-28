package org.acme

import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/classrooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ClassroomResource {

    @Inject
    lateinit var em: EntityManager

    @POST
    @Transactional
    fun createClassroom(request: CreateClassroomRequest): Response {
        // Validate classroom name is not empty
        if (request.name.isBlank()) {
            return Response.status(400)
                .entity(mapOf("error" to "Classroom name cannot be empty"))
                .build()
        }

        // Validate classroom name does not exceed 100 characters
        if (request.name.length > 100) {
            return Response.status(400)
                .entity(mapOf("error" to "Classroom name cannot exceed 100 characters"))
                .build()
        }

        // Create and persist classroom entity
        val classroom = Classroom()
        classroom.name = request.name.trim()
        classroom.description = request.description?.trim()
        em.persist(classroom)

        // Return 201 with ClassroomDetailResponse
        val response = ClassroomDetailResponse(
            id = classroom.id!!,
            name = classroom.name,
            description = classroom.description,
            urls = emptyList(),
            createdAt = classroom.createdAt.toString()
        )

        return Response.status(201).entity(response).build()
    }

    @GET
    fun listClassrooms(): List<ClassroomListResponse> {
        // Query all classrooms ordered by creation date (newest first)
        // Use a join to calculate URL count for each classroom
        val query = em.createQuery(
            """
            SELECT c.id, c.name, c.description, c.createdAt, COUNT(u.id)
            FROM Classroom c
            LEFT JOIN c.urls u
            GROUP BY c.id, c.name, c.description, c.createdAt
            ORDER BY c.createdAt DESC
            """,
            Array<Any>::class.java
        )
        
        val results = query.resultList
        
        return results.map { row ->
            val data = row as Array<*>
            ClassroomListResponse(
                id = data[0] as Long,
                name = data[1] as String,
                description = data[2] as String?,
                urlCount = data[4] as Long,
                createdAt = data[3].toString()
            )
        }
    }

    @GET
    @Path("/{id}")
    fun getClassroom(@PathParam("id") id: Long): Response {
        // Query classroom by ID with eager fetch of URLs
        val classroom = em.createQuery(
            "SELECT c FROM Classroom c LEFT JOIN FETCH c.urls WHERE c.id = :id",
            Classroom::class.java
        )
            .setParameter("id", id)
            .resultList
            .firstOrNull()

        // Return 404 if classroom not found
        if (classroom == null) {
            return Response.status(404)
                .entity(mapOf("error" to "Classroom not found"))
                .build()
        }

        // Map URLs to UrlResponse
        val urlResponses = classroom.urls.map { url ->
            UrlResponse(
                id = url.id!!,
                url = url.url,
                urlType = url.urlType,
                createdAt = url.createdAt.toString()
            )
        }

        // Return 200 with ClassroomDetailResponse including all URLs
        val response = ClassroomDetailResponse(
            id = classroom.id!!,
            name = classroom.name,
            description = classroom.description,
            urls = urlResponses,
            createdAt = classroom.createdAt.toString()
        )

        return Response.status(200).entity(response).build()
    }

    @POST
    @Path("/{id}/urls")
    @Transactional
    fun addUrl(@PathParam("id") id: Long, request: AddUrlRequest): Response {
        // Validate URL is not empty
        if (request.url.isBlank()) {
            return Response.status(400)
                .entity(mapOf("error" to "URL cannot be empty"))
                .build()
        }

        // Validate URL format
        if (!UrlValidator.isValid(request.url)) {
            return Response.status(400)
                .entity(mapOf("error" to "Invalid URL format. Please provide a valid domain (e.g., example.com)"))
                .build()
        }

        // Normalize URL
        val normalizedUrl = UrlValidator.normalize(request.url)

        // Check if classroom exists
        val classroom = em.find(Classroom::class.java, id)
        if (classroom == null) {
            return Response.status(404)
                .entity(mapOf("error" to "Classroom not found"))
                .build()
        }

        // Check for duplicate URL in classroom
        val existingUrl = em.createQuery(
            "SELECT cu FROM ClassroomUrl cu WHERE cu.classroom.id = :classroomId AND cu.url = :url",
            ClassroomUrl::class.java
        )
            .setParameter("classroomId", id)
            .setParameter("url", normalizedUrl)
            .resultList
            .firstOrNull()

        if (existingUrl != null) {
            return Response.status(400)
                .entity(mapOf("error" to "This URL already exists in the classroom"))
                .build()
        }

        // Create ClassroomUrl entity
        val classroomUrl = ClassroomUrl()
        classroomUrl.classroom = classroom
        classroomUrl.url = normalizedUrl
        classroomUrl.urlType = "whitelist"
        em.persist(classroomUrl)

        // Return 201 with UrlResponse
        val response = UrlResponse(
            id = classroomUrl.id!!,
            url = classroomUrl.url,
            urlType = classroomUrl.urlType,
            createdAt = classroomUrl.createdAt.toString()
        )

        return Response.status(201).entity(response).build()
    }
}
