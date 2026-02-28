package org.acme

import java.net.URI

object UrlValidator {
    private val domainPattern = Regex(
        "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
    )
    
    fun isValid(url: String): Boolean {
        val normalized = normalize(url)
        return try {
            val uri = URI(normalized)
            uri.host != null && domainPattern.matches(uri.host)
        } catch (e: Exception) {
            false
        }
    }
    
    fun normalize(url: String): String {
        var normalized = url.trim().lowercase()
        
        // Add protocol if missing
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://$normalized"
        }
        
        return normalized
    }
    
    fun extractDomain(url: String): String {
        val normalized = normalize(url)
        return try {
            URI(normalized).host ?: url
        } catch (e: Exception) {
            url
        }
    }
}
