package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This object allows you to have a blind carbon copy
 * automatically sent to the specified email address
 * for every email that is sent.
 */
@JsonInclude(Include.NON_EMPTY)
data class BccSettings @JvmOverloads constructor(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("email")
        @get:JsonProperty("email")
        var email: String? = null
)