package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A setting object that allows you to test the content of
 * your email for spam.
 */
@JsonInclude(Include.NON_EMPTY)
data class SpamCheckSetting @JvmOverloads constructor(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("threshold")
        @get:JsonProperty("threshold")
        var spamThreshold: Int = 0,
        @JsonProperty("post_to_url")
        @get:JsonProperty("post_to_url")
        var postToUrl: String? = null
)