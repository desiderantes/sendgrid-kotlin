package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(Include.NON_DEFAULT)
data class MailSettings @JvmOverloads constructor(
        @JsonProperty("bcc")
        @get:JsonProperty("bcc")
        var bccSettings: BccSettings? = null,
        @JsonProperty("bypass_list_management")
        @get:JsonProperty("bypass_list_management")
        var bypassListManagement: Setting? = null,
        @JsonProperty("footer")
        @get:JsonProperty("footer")
        var footerSetting: FooterSetting? = null,
        @JsonProperty("sandbox_mode")
        @get:JsonProperty("sandbox_mode")
        var sandBoxMode: Setting? = null,
        @JsonProperty("spam_check")
        @get:JsonProperty("spam_check")
        var spamCheck: SpamCheckSetting? = null
)