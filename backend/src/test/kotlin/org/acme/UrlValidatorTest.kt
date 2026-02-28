package org.acme

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@QuarkusTest
class UrlValidatorTest {

    @Test
    fun `should accept domain without protocol`() {
        assertTrue(UrlValidator.isValid("example.com"))
    }

    @Test
    fun `should accept domain with https protocol`() {
        assertTrue(UrlValidator.isValid("https://example.com"))
    }

    @Test
    fun `should normalize uppercase domain to lowercase`() {
        val normalized = UrlValidator.normalize("EXAMPLE.COM")
        assertEquals("https://example.com", normalized)
    }

    @Test
    fun `should add https protocol when missing`() {
        val normalized = UrlValidator.normalize("example.com")
        assertEquals("https://example.com", normalized)
    }

    @Test
    fun `should reject empty string`() {
        assertFalse(UrlValidator.isValid(""))
    }

    @Test
    fun `should reject domain without TLD`() {
        assertFalse(UrlValidator.isValid("example"))
    }

    @Test
    fun `should extract domain from simple URL`() {
        val domain = UrlValidator.extractDomain("https://example.com")
        assertEquals("example.com", domain)
    }
}
