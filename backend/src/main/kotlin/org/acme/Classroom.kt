package org.acme

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Classroom entity represents a classroom in the system.
 * 
 * A classroom is a logical grouping representing a physical class or lesson group.
 * Each classroom can have multiple URLs associated with it for DNS filtering control.
 * 
 * This entity supports the Classroom URL Management feature, enabling teachers to
 * organize URL lists by class or subject.
 */
@Entity
@Table(name = "classrooms")
class Classroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @Column(nullable = false, length = 100)
    var name: String = ""
    
    @Column(length = 500)
    var description: String? = null
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
    
    @OneToMany(mappedBy = "classroom", cascade = [CascadeType.ALL], orphanRemoval = true)
    var urls: MutableList<ClassroomUrl> = mutableListOf()
}
