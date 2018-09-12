package com.sendgrid.tests

import com.sendgrid.Content
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContentTest : Spek({

    describe("Content") {
        val content = Content()

        it(" rejects forbidden content") {

            val sampleApiKeys = arrayListOf(
                    "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",
                    "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"
            )

            for (apiKey in sampleApiKeys) {
                assertFailsWith<IllegalArgumentException> {
                    content.value = "My api key is: $apiKey"
                }
            }
        }

        it("allows normal content") {
            val message = "I will not send you my api key!"
            content.value = message
            assertEquals(message, content.value)
        }
    }
})
