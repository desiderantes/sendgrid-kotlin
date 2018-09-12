package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.HashMap


@JsonInclude(Include.NON_DEFAULT)
data class Personalization @JvmOverloads constructor(
        @JsonProperty("to")
        var tos: MutableList<Email> = mutableListOf(),
        @JsonProperty("cc")
        var ccs: MutableList<Email> = mutableListOf(),
        @JsonProperty("bcc")
        var bccs: MutableList<Email> = mutableListOf(),
        @JsonProperty("subject")
        @get:JsonProperty("subject")
        var subject: String? = null,
        @JsonProperty("headers")
        var headers: MutableMap<String, String> = mutableMapOf(),
        @JsonProperty("substitutions")
        var substitutions: MutableMap<String, String> = mutableMapOf(),
        @JsonProperty("custom_args")
        var customArgs: MutableMap<String, String> = mutableMapOf(),
        @JsonProperty("dynamic_template_data")
        var dynamicTemplateData: MutableMap<String, Any> = mutableMapOf(),
        @JsonProperty("send_at")
        var sendAt: Long = 0) {

    /** This class adds a copy of the passed parameter, so calling it multiple times with the same object will generate multiple copies of it */
    fun addTo(email: Email) {
        tos.add(email.copy())
    }

    /** This class adds a copy of the passed parameter, so calling it multiple times with the same object will generate multiple copies of it */
    fun addCc(email: Email) {
        ccs.add(email.copy())
    }

    /** This class adds a copy of the passed parameter, so calling it multiple times with the same object will generate multiple copies of it */
    fun addBcc(email: Email) {
        bccs.add(email.copy())
    }

    fun addHeader(key: String, value: String) {
        headers[key] = value
    }

    fun addSubstitution(key: String, value: String) {
        substitutions[key] = value
    }

    fun addDynamicTemplateData(key: String, value: Any) {
        dynamicTemplateData[key] = value
    }

    fun addCustomArg(key: String, value: String) {
        customArgs[key] = value
    }
}
