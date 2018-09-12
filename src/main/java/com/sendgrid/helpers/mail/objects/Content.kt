package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.regex.Pattern

/**
 * An object in which you may specify the content of your email.
 */
@JsonInclude(Include.NON_DEFAULT)
class Content(@JsonProperty("type") var type: String = "text/plain", value: String = "") {
    @JsonProperty("value")
    var value: String = value
        set(value) {
            verifyContent(value)
            field = value
        }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + type.hashCode()
        result = prime * result + this.value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val other = other as Content?
        if (type != other!!.type)
            return false
        if (this.value != other.value)
            return false
        return true
    }

    fun copy(type: String = this.type, value: String = this.value): Content {
        return Content(type = type, value = value)
    }

    companion object ContentVerifier {
        private val FORBIDDEN_PATTERNS = listOf(Pattern.compile(".*SG\\.[a-zA-Z0-9(-|_)]*\\.[a-zA-Z0-9(-|_)]*.*"))

        private fun verifyContent(content: String) {
            if (FORBIDDEN_PATTERNS.any { pattern -> pattern.matcher(content).matches() }) {
                throw IllegalArgumentException("Found a Forbidden Pattern in the content of the email")
            }
        }
    }
}