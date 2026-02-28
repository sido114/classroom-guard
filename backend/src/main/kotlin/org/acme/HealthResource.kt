package org.acme

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
class HealthResource {

    @GET
    fun health(): HealthStatus {
        return HealthStatus(status = "UP")
    }
}

data class HealthStatus(
    val status: String
)
