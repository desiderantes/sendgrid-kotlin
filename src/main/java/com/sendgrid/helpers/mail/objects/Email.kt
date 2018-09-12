package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonInclude(Include.NON_DEFAULT)
data class Email @JvmOverloads constructor(
        @JsonProperty("email")
        @get:JsonProperty("email")
        var email: String? = null,
        @JsonProperty("name")
        @get:JsonProperty("name")
        var name: String? = null
) : Serializable