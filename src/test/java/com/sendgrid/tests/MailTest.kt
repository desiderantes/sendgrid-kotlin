package com.sendgrid.tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sendgrid.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MailTest : Spek({

    describe("Mail") {
        it("can be constructed") {
            val from = Email("test@example.com")
            val subject = "Sending with SendGrid is Fun"
            val to = Email("test@example.com")
            val content = Content("text/plain", "and easy to do anywhere, even with Java")
            val mail = Mail(from, subject, to, content)

            assertEquals("{\"from\":{\"email\":\"test@example.com\"},\"subject\":\"Sending with SendGrid is Fun\",\"personalizations\":[{\"to\":[{\"email\":\"test@example.com\"}]}],\"content\":[{\"type\":\"text/plain\",\"value\":\"and easy to do anywhere, even with Java\"}]}", mail.build())
        }

        it("can be constructed with everything but the kitchen sink") {
            val mail = Mail()

            mail.from = Email(
                    name = "Example User",
                    email = "test@example.com"
            )

            mail.subject = "Hello World from the SendGrid Java Library"

            val personalization = Personalization()
            val to = Email(
                    name = "Example User",
                    email = "test@example.com"
            )
            personalization.addTo(to)
            personalization.addTo(to)
            personalization.addCc(to)
            personalization.addCc(to)
            personalization.addBcc(to)
            personalization.addBcc(to)
            personalization.subject = "Hello World from the Personalized SendGrid Java Library"
            personalization.addHeader("X-Test", "test")
            personalization.addHeader("X-Mock", "true")
            personalization.addSubstitution("%name%", "Example User")
            personalization.addSubstitution("%city%", "Denver")
            personalization.addCustomArg("user_id", "343")
            personalization.addCustomArg("type", "marketing")
            personalization.sendAt = 1443636843
            mail.addPersonalization(personalization)

            val personalization2 = Personalization()
            personalization2.addTo(to)
            personalization2.addTo(to)
            personalization2.addCc(to)
            personalization2.addCc(to)
            personalization2.addBcc(to)
            personalization2.addBcc(to)
            personalization2.subject = "Hello World from the Personalized SendGrid Java Library"
            personalization2.addHeader("X-Test", "test")
            personalization2.addHeader("X-Mock", "true")
            personalization2.addSubstitution("%name%", "Example User")
            personalization2.addSubstitution("%city%", "Denver")
            personalization2.addCustomArg("user_id", "343")
            personalization2.addCustomArg("type", "marketing")
            personalization2.sendAt = 1443636843
            mail.addPersonalization(personalization2)

            val personalization3 = Personalization()
            personalization3.addTo(to)
            personalization3.addTo(to)
            personalization3.addCc(to)
            personalization3.addCc(to)
            personalization3.addBcc(to)
            personalization3.addBcc(to)
            personalization3.subject = "Hello World from the Personalized SendGrid Java Library"
            personalization3.addHeader("X-Test", "test")
            personalization3.addHeader("X-Mock", "true")
            personalization3.addSubstitution("%name%", "Example User")
            personalization3.addSubstitution("%city%", "Denver")
            personalization3.addDynamicTemplateData("name", "Example User")
            personalization3.addDynamicTemplateData("city", "Denver")
            personalization3.addDynamicTemplateData("items", listOf(
                    object {
                        val text = "New Line Sneakers"
                        val price = "$ 79.95"
                    },
                    object {
                        val text = "Old Line Sneakers"
                        val price = "$ 59.95"
                    }

            ))
            personalization3.addCustomArg("user_id", "343")
            personalization3.addCustomArg("type", "marketing")
            personalization3.sendAt = 1443636843
            mail.addPersonalization(personalization3)

            mail.addContent(Content(
                    type = "text/plain",
                    value = "some text here"
            ))
            mail.addContent(Content(
                    type = "text/html",
                    value = "<html><body>some text here</body></html>"
            ))

            val attachments = Attachments(
                    content = "TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gQ3JhcyBwdW12",
                    type = "application/pdf",
                    filename = "balance_001.pdf",
                    disposition = "attachment",
                    contentId = "Balance Sheet"
            )
            mail.addAttachments(attachments)

            val attachments2 = Attachments(
                    content = "BwdW",
                    type = "image/png",
                    filename = "banner.png",
                    disposition = "inline",
                    contentId = "Banner"
            )
            mail.addAttachments(attachments2)

            mail.templateId = "13b8f94f-bcae-4ec6-b752-70d6cb59f932"

            mail.addSection("%section1%", "Substitution Text for Section 1")
            mail.addSection("%section2%", "Substitution Text for Section 2")

            mail.addHeader("X-Test1", "1")
            mail.addHeader("X-Test2", "2")

            mail.addCategory("May")
            mail.addCategory("2016")

            mail.addCustomArg("campaign", "welcome")
            mail.addCustomArg("weekday", "morning")

            mail.sendAt = 1443636842

            mail.asm = ASM(
                    groupId = 99,
                    groupsToDisplay = intArrayOf(4, 5, 6, 7, 8))

            // This must be a valid [batch ID](https://sendgrid.com/docs/API_Reference/SMTP_API/scheduling_parameters.html) to work
            // mail.setBatchId("sendgrid_batch_id");

            mail.ipPoolId = "23"
            mail.mailSettings = MailSettings(
                    bccSettings = BccSettings(
                            enable = true,
                            email = "test@example.com"
                    ),
                    sandBoxMode = Setting(
                            enable = true
                    ),
                    bypassListManagement = Setting(
                            enable = true
                    ),
                    footerSetting = FooterSetting(
                            enable = true,
                            text = "Footer Text",
                            html = "<html><body>Footer Text</body></html>"
                    ),
                    spamCheck = SpamCheckSetting(
                            enable = true,
                            spamThreshold = 1,
                            postToUrl = "https://spamcatcher.sendgrid.com"
                    )
            )

            mail.trackingSettings = TrackingSettings(
                    clickTrackingSetting = ClickTrackingSetting(
                            enable = true,
                            enableText = false),
                    openTrackingSetting = OpenTrackingSetting(
                            enable = true,
                            substitutionTag = "Optional tag to replace with the open image in the body of the message"
                    ),
                    subscriptionTrackingSetting = SubscriptionTrackingSetting(
                            enable = true,
                            text = "text to insert into the text/plain portion of the message",
                            html = "<html><body>html to insert into the text/html portion of the message</body></html>",
                            substitutionTag = "Optional tag to replace with the open image in the body of the message"),
                    googleAnalyticsSetting = GoogleAnalyticsSetting(
                            enable = true,
                            campaignSource = "some source",
                            campaignTerm = "some term",
                            campaignContent = "some content",
                            campaignName = "some name",
                            campaignMedium = "some medium")
            )

            mail.replyTo = Email(
                    name = "Example User",
                    email = "test@example.com"
            )

            assertEquals("{\"from\":{\"name\":\"Example User\",\"email\":\"test@example.com\"},\"subject\":\"Hello World from the SendGrid Java Library\",\"personalizations\":[{\"to\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"cc\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"bcc\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"subject\":\"Hello World from the Personalized SendGrid Java Library\",\"headers\":{\"X-Mock\":\"true\",\"X-Test\":\"test\"},\"substitutions\":{\"%city%\":\"Denver\",\"%name%\":\"Example User\"},\"custom_args\":{\"type\":\"marketing\",\"user_id\":\"343\"},\"send_at\":1443636843},{\"to\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"cc\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"bcc\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"subject\":\"Hello World from the Personalized SendGrid Java Library\",\"headers\":{\"X-Mock\":\"true\",\"X-Test\":\"test\"},\"substitutions\":{\"%city%\":\"Denver\",\"%name%\":\"Example User\"},\"custom_args\":{\"type\":\"marketing\",\"user_id\":\"343\"},\"send_at\":1443636843},{\"to\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"cc\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"bcc\":[{\"name\":\"Example User\",\"email\":\"test@example.com\"},{\"name\":\"Example User\",\"email\":\"test@example.com\"}],\"subject\":\"Hello World from the Personalized SendGrid Java Library\",\"headers\":{\"X-Mock\":\"true\",\"X-Test\":\"test\"},\"custom_args\":{\"type\":\"marketing\",\"user_id\":\"343\"},\"dynamic_template_data\":{\"city\":\"Denver\",\"items\":[{\"price\":\"$ 59.95\",\"text\":\"New Line Sneakers\"},{\"text\":\"Old Line Sneakers\"}],\"name\":\"Example User\"},\"send_at\":1443636843}],\"content\":[{\"type\":\"text/plain\",\"value\":\"some text here\"},{\"type\":\"text/html\",\"value\":\"<html><body>some text here</body></html>\"}],\"attachments\":[{\"content\":\"TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gQ3JhcyBwdW12\",\"type\":\"application/pdf\",\"filename\":\"balance_001.pdf\",\"disposition\":\"attachment\",\"content_id\":\"Balance Sheet\"},{\"content\":\"BwdW\",\"type\":\"image/png\",\"filename\":\"banner.png\",\"disposition\":\"inline\",\"content_id\":\"Banner\"}],\"template_id\":\"13b8f94f-bcae-4ec6-b752-70d6cb59f932\",\"sections\":{\"%section1%\":\"Substitution Text for Section 1\",\"%section2%\":\"Substitution Text for Section 2\"},\"headers\":{\"X-Test1\":\"1\",\"X-Test2\":\"2\"},\"categories\":[\"May\",\"2016\"],\"custom_args\":{\"campaign\":\"welcome\",\"weekday\":\"morning\"},\"send_at\":1443636842,\"asm\":{\"group_id\":99,\"groups_to_display\":[4,5,6,7,8]},\"ip_pool_name\":\"23\",\"mail_settings\":{\"bcc\":{\"enable\":true,\"email\":\"test@example.com\"},\"bypass_list_management\":{\"enable\":true},\"footer\":{\"enable\":true,\"text\":\"Footer Text\",\"html\":\"<html><body>Footer Text</body></html>\"},\"sandbox_mode\":{\"enable\":true},\"spam_check\":{\"enable\":true,\"threshold\":1,\"post_to_url\":\"https://spamcatcher.sendgrid.com\"}},\"tracking_settings\":{\"click_tracking\":{\"enable\":true,\"enable_text\":false},\"open_tracking\":{\"enable\":true,\"substitution_tag\":\"Optional tag to replace with the open image in the body of the message\"},\"subscription_tracking\":{\"enable\":true,\"text\":\"text to insert into the text/plain portion of the message\",\"html\":\"<html><body>html to insert into the text/html portion of the message</body></html>\",\"substitution_tag\":\"Optional tag to replace with the open image in the body of the message\"},\"ganalytics\":{\"enable\":true,\"utm_source\":\"some source\",\"utm_term\":\"some term\",\"utm_content\":\"some content\",\"utm_campaign\":\"some name\",\"utm_medium\":\"some medium\"}},\"reply_to\":{\"name\":\"Example User\",\"email\":\"test@example.com\"}}", mail.build())
        }

        it("should return the correct 'from'") {
            val mail = Mail()
            val from = Email()
            mail.from = from

            assertSame(from, mail.from)
        }

        it("can be properly deserialized") {
            val to = Email("foo@bar.com")
            val content = Content("text/plain", "test")
            val from = Email("no-reply@bar.com")
            val mail = Mail(from, "subject", to, content)

            val mapper = jacksonObjectMapper()
            val json = mapper.writeValueAsString(mail)
            val deserialized = mapper.readValue<Mail>(json)

            assertEquals(deserialized, mail)
        }
    }
})
