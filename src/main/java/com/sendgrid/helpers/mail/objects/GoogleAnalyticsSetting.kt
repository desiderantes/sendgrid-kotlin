package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An object configuring the tracking provided by Google Analytics.
 */
@JsonInclude(Include.NON_EMPTY)
data class GoogleAnalyticsSetting @JvmOverloads constructor(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("utm_source")
        @get:JsonProperty("utm_source")
        var campaignSource: String? = null,
        @JsonProperty("utm_term")
        @get:JsonProperty("utm_term")
        var campaignTerm: String? = null,
        @JsonProperty("utm_content")
        @get:JsonProperty("utm_content")
        var campaignContent: String? = null,
        @JsonProperty("utm_campaign")
        @get:JsonProperty("utm_campaign")
        var campaignName: String? = null,
        @JsonProperty("utm_medium")
        @get:JsonProperty("utm_medium")
        var campaignMedium: String? = null
)