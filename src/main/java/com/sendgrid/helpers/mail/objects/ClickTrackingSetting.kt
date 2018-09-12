package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Settings to determine how you would like to track the
 * metrics of how your recipients interact with your email.
 */
@JsonInclude(Include.NON_EMPTY)
class ClickTrackingSetting(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("enable_text")
        @get:JsonProperty("enable_text")
        var enableText: Boolean = false
)