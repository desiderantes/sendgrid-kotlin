package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(Include.NON_DEFAULT)
class TrackingSettings @JvmOverloads constructor(
        @JsonProperty("click_tracking")
        @get:JsonProperty("click_tracking")
        var clickTrackingSetting: ClickTrackingSetting? = null,
        @JsonProperty("open_tracking")
        @get:JsonProperty("open_tracking")
        var openTrackingSetting: OpenTrackingSetting? = null,
        @JsonProperty("subscription_tracking")
        @get:JsonProperty("subscription_tracking")
        var subscriptionTrackingSetting: SubscriptionTrackingSetting? = null,
        @JsonProperty("ganalytics")
        @get:JsonProperty("ganalytics")
        var googleAnalyticsSetting: GoogleAnalyticsSetting? = null
)