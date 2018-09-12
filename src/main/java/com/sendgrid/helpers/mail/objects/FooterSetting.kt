package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An object representing the default footer
 * that you would like included on every email.
 */
@JsonInclude(Include.NON_EMPTY)
class FooterSetting @JvmOverloads constructor(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("text")
        @get:JsonProperty("text")
        var text: String? = null,
        @JsonProperty("html")
        @get:JsonProperty("html")
        var html: String? = null
)