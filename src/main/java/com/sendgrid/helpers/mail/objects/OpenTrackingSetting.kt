package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An open tracking settings object. This allows you to track
 * whether the email was opened or not, but including a single
 * pixel image in the body of the content. When the pixel is
 * loaded, we can log that the email was opened.
 */
@JsonInclude(Include.NON_EMPTY)
data class OpenTrackingSetting @JvmOverloads constructor(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("substitution_tag")
        @get:JsonProperty("substitution_tag")
        var substitutionTag: String? = null
)