package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A subscription tracking setting object. Subscription tracking
 * allows you to insert a subscription management link at the
 * bottom of the text and html bodies of your email. If you
 * would like to specify the location of the link within your
 * email, you may use the substitution_tag.
 */
@JsonInclude(Include.NON_EMPTY)
data class SubscriptionTrackingSetting @JvmOverloads constructor(
        @JsonProperty("enable")
        @get:JsonProperty("enable")
        var enable: Boolean = false,
        @JsonProperty("text")
        @get:JsonProperty("text")
        var text: String? = null,
        @JsonProperty("html")
        @get:JsonProperty("html")
        var html: String? = null,
        @JsonProperty("substitution_tag")
        @get:JsonProperty("substitution_tag")
        var substitutionTag: String? = null
)