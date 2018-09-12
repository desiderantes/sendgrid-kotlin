package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_DEFAULT)
data class Setting @JvmOverloads constructor(
        var enable: Boolean = false
)