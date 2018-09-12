package com.sendgrid.tests.objects

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sendgrid.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class SettingsSerializationTest : Spek({

    val mapper = jacksonObjectMapper()

    given("Settings Serialization") {


        on("OpenTrackingSetting") {
            val setting = OpenTrackingSetting()
            setting.enable = false

            val json = mapper.writeValueAsString(setting)
            assertEquals(json, "{\"enable\":false}")
        }

        on("ClickTrackingSetting") {
            val setting = ClickTrackingSetting()
            setting.enable = false

            val json = mapper.writeValueAsString(setting)
            println(json)
            assertEquals(json, "{\"enable\":false,\"enable_text\":false}")
        }

        on("SubscriptionTrackingSetting") {
            val setting = SubscriptionTrackingSetting()
            setting.enable = false

            val json = mapper.writeValueAsString(setting)
            println(json)
            assertEquals(json, "{\"enable\":false}")
        }

        on("GoogleAnalyticsTrackingSetting") {
            val setting = GoogleAnalyticsSetting()
            setting.enable = false

            val json = mapper.writeValueAsString(setting)
            println(json)
            assertEquals(json, "{\"enable\":false}")
        }

        on("SpamCheckSetting") {
            val setting = SpamCheckSetting()
            setting.enable = false

            val json = mapper.writeValueAsString(setting)
            println(json)
            assertEquals(json, "{\"enable\":false,\"threshold\":0}")
        }

        on("FooterSetting") {
            val setting = FooterSetting()
            setting.enable = false

            val json = mapper.writeValueAsString(setting)
            println(json)
            assertEquals(json, "{\"enable\":false}")
        }

        on("BccSettings") {
            val settings = BccSettings()
            settings.enable = false

            val json = mapper.writeValueAsString(settings)
            println(json)
            assertEquals(json, "{\"enable\":false}")
        }
    }
})
