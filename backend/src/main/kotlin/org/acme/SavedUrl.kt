package org.acme

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * SavedUrl entity represents a URL stored in the database.
 * 
 * This entity is used by the URL Dashboard feature to persist
 * URLs submitted by users for later retrieval.
 */
@Entity
@Table(name = "saved_urls")
class SavedUrl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @Column(nullable = false)
    var url: String = ""
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
