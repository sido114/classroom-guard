package org.acme

import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/urls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SavedUrlResource {

    @Inject
    lateinit var em: EntityManager

    @GET
    fun list(): List<SavedUrl> {
        return em.createQuery("SELECT s FROM SavedUrl s", SavedUrl::class.java).resultList
    }

    @POST
    @Transactional
    fun create(request: CreateUrlRequest): Response {
        // Validate URL is not empty
        if (request.url.isBlank()) {
            return Response.status(400)
                .entity(mapOf("error" to "URL cannot be empty"))
                .build()
        }
        
        val savedUrl = SavedUrl()
        savedUrl.url = request.url
        em.persist(savedUrl)
        return Response.status(201).entity(savedUrl).build()
    }
}

data class CreateUrlRequest(
    val url: String
)
