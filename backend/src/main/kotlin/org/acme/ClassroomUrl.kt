package org.acme

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * ClassroomUrl entity represents a URL associated with a classroom.
 * 
 * Each URL belongs to exactly one classroom and is used for DNS filtering control.
 * URLs are normalized before storage (lowercase, protocol added if missing) and
 * duplicate URLs within the same classroom are prevented by a unique constraint.
 * 
 * The urlType field supports future whitelist/blacklist functionality, defaulting
 * to "whitelist" for the current implementation.
 */
@Entity
@Table(
    name = "classroom_urls",
    uniqueConstraints = [UniqueConstraint(columnNames = ["classroom_id", "url"])]
)
class ClassroomUrl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    var classroom: Classroom? = null
    
    @Column(nullable = false, length = 2048)
    var url: String = ""
    
    @Column(name = "url_type", length = 20)
    var urlType: String = "whitelist"
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
