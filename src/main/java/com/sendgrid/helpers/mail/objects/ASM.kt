package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonInclude(Include.NON_DEFAULT)
data class ASM(
        @JsonProperty("group_id")
        @get:JsonProperty("group_id")
        var groupId: Int = 0,
        @JsonProperty("groups_to_display")
        @get:JsonProperty("groups_to_display")
        var groupsToDisplay: IntArray = intArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ASM

        if (groupId != other.groupId) return false
        if (!Arrays.equals(groupsToDisplay, other.groupsToDisplay)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId
        result = 31 * result + groupsToDisplay.let { Arrays.hashCode(it) }
        return result
    }
}