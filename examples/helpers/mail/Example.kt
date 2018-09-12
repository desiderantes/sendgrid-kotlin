import com.sendgrid.ASM
import com.sendgrid.Attachments
import com.sendgrid.BccSettings
import com.sendgrid.ClickTrackingSetting
import com.sendgrid.Client
import com.sendgrid.Content
import com.sendgrid.Email
import com.sendgrid.FooterSetting
import com.sendgrid.GoogleAnalyticsSetting
import com.sendgrid.Mail
import com.sendgrid.MailSettings
import com.sendgrid.Method
import com.sendgrid.OpenTrackingSetting
import com.sendgrid.Personalization
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.Setting
import com.sendgrid.SpamCheckSetting
import com.sendgrid.SubscriptionTrackingSetting
import com.sendgrid.TrackingSettings

import java.io.IOException
import java.util.HashMap

object Example {

    // Fully populated Mail object
    @Throws(IOException::class)
    fun buildKitchenSink(): Mail {
        val mail = Mail()

        val fromEmail = Email()
        fromEmail.setName("Example User")
        fromEmail.setEmail("test@example.com")
        mail.setFrom(fromEmail)

        mail.setSubject("Hello World from the SendGrid Java Library")

        val personalization = Personalization()
        val to = Email()
        to.setName("Example User")
        to.setEmail("test1@example.com")
        personalization.addTo(to)
        to.setName("Example User")
        to.setEmail("test2@example.com")
        personalization.addTo(to)
        val cc = Email()
        cc.setName("Example User")
        cc.setEmail("test3@example.com")
        personalization.addCc(cc)
        cc.setName("Example User")
        cc.setEmail("test4@example.com")
        personalization.addCc(cc)
        val bcc = Email()
        bcc.setName("Example User")
        bcc.setEmail("test5@example.com")
        personalization.addBcc(bcc)
        bcc.setName("Example User")
        bcc.setEmail("test6@example.com")
        personalization.addBcc(bcc)
        personalization.setSubject("Hello World from the Personalized SendGrid Java Library")
        personalization.addHeader("X-Test", "test")
        personalization.addHeader("X-Mock", "true")
        personalization.addSubstitution("%name%", "Example User")
        personalization.addSubstitution("%city%", "Riverside")
        personalization.addCustomArg("user_id", "343")
        personalization.addCustomArg("type", "marketing")
        personalization.setSendAt(1443636843)
        mail.addPersonalization(personalization)

        val personalization2 = Personalization()
        val to2 = Email()
        to2.setName("Example User")
        to2.setEmail("test1@example.com")
        personalization2.addTo(to2)
        to2.setName("Example User")
        to2.setEmail("test2@example.com")
        personalization2.addTo(to2)
        val cc2 = Email()
        cc2.setName("Example User")
        cc2.setEmail("test3@example.com")
        personalization2.addCc(cc2)
        cc2.setName("Example User")
        cc2.setEmail("test4@example.com")
        personalization2.addCc(cc2)
        val bcc2 = Email()
        bcc2.setName("Example User")
        bcc2.setEmail("test5@example.com")
        personalization2.addBcc(bcc2)
        bcc2.setName("Example User")
        bcc2.setEmail("test6@example.com")
        personalization2.addBcc(bcc2)
        personalization2.setSubject("Hello World from the Personalized SendGrid Java Library")
        personalization2.addHeader("X-Test", "test")
        personalization2.addHeader("X-Mock", "true")
        personalization2.addSubstitution("%name%", "Example User")
        personalization2.addSubstitution("%city%", "Denver")
        personalization2.addCustomArg("user_id", "343")
        personalization2.addCustomArg("type", "marketing")
        personalization2.setSendAt(1443636843)
        mail.addPersonalization(personalization2)

        val content = Content()
        content.setType("text/plain")
        content.setValue("some text here")
        mail.addContent(content)
        content.setType("text/html")
        content.setValue("<html><body>some text here</body></html>")
        mail.addContent(content)

        val attachments = Attachments()
        attachments.setContent("TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gQ3JhcyBwdW12")
        attachments.setType("application/pdf")
        attachments.setFilename("balance_001.pdf")
        attachments.setDisposition("attachment")
        attachments.setContentId("Balance Sheet")
        mail.addAttachments(attachments)

        val attachments2 = Attachments()
        attachments2.setContent("BwdW")
        attachments2.setType("image/png")
        attachments2.setFilename("banner.png")
        attachments2.setDisposition("inline")
        attachments2.setContentId("Banner")
        mail.addAttachments(attachments2)

        mail.setTemplateId("13b8f94f-bcae-4ec6-b752-70d6cb59f932")

        mail.addSection("%section1%", "Substitution Text for Section 1")
        mail.addSection("%section2%", "Substitution Text for Section 2")

        mail.addHeader("X-Test1", "1")
        mail.addHeader("X-Test2", "2")

        mail.addCategory("May")
        mail.addCategory("2016")

        mail.addCustomArg("campaign", "welcome")
        mail.addCustomArg("weekday", "morning")

        mail.setSendAt(1443636842)

        val asm = ASM()
        asm.setGroupId(99)
        asm.setGroupsToDisplay(intArrayOf(4, 5, 6, 7, 8))
        mail.setASM(asm)

        // This must be a valid [batch ID](https://sendgrid.com/docs/API_Reference/SMTP_API/scheduling_parameters.html) to work
        // mail.setBatchId("sendgrid_batch_id");

        mail.setIpPoolId("23")

        val mailSettings = MailSettings()
        val bccSettings = BccSettings()
        bccSettings.setEnable(true)
        bccSettings.setEmail("test@example.com")
        mailSettings.setBccSettings(bccSettings)
        val sandBoxMode = Setting()
        sandBoxMode.setEnable(true)
        mailSettings.setSandboxMode(sandBoxMode)
        val bypassListManagement = Setting()
        bypassListManagement.setEnable(true)
        mailSettings.setBypassListManagement(bypassListManagement)
        val footerSetting = FooterSetting()
        footerSetting.setEnable(true)
        footerSetting.setText("Footer Text")
        footerSetting.setHtml("<html><body>Footer Text</body></html>")
        mailSettings.setFooterSetting(footerSetting)
        val spamCheckSetting = SpamCheckSetting()
        spamCheckSetting.setEnable(true)
        spamCheckSetting.setSpamThreshold(1)
        spamCheckSetting.setPostToUrl("https://spamcatcher.sendgrid.com")
        mailSettings.setSpamCheckSetting(spamCheckSetting)
        mail.setMailSettings(mailSettings)

        val trackingSettings = TrackingSettings()
        val clickTrackingSetting = ClickTrackingSetting()
        clickTrackingSetting.setEnable(true)
        clickTrackingSetting.setEnableText(true)
        trackingSettings.setClickTrackingSetting(clickTrackingSetting)
        val openTrackingSetting = OpenTrackingSetting()
        openTrackingSetting.setEnable(true)
        openTrackingSetting.setSubstitutionTag("Optional tag to replace with the open image in the body of the message")
        trackingSettings.setOpenTrackingSetting(openTrackingSetting)
        val subscriptionTrackingSetting = SubscriptionTrackingSetting()
        subscriptionTrackingSetting.setEnable(true)
        subscriptionTrackingSetting.setText("text to insert into the text/plain portion of the message")
        subscriptionTrackingSetting.setHtml("<html><body>html to insert into the text/html portion of the message</body></html>")
        subscriptionTrackingSetting.setSubstitutionTag("Optional tag to replace with the open image in the body of the message")
        trackingSettings.setSubscriptionTrackingSetting(subscriptionTrackingSetting)
        val googleAnalyticsSetting = GoogleAnalyticsSetting()
        googleAnalyticsSetting.setEnable(true)
        googleAnalyticsSetting.setCampaignSource("some source")
        googleAnalyticsSetting.setCampaignTerm("some term")
        googleAnalyticsSetting.setCampaignContent("some content")
        googleAnalyticsSetting.setCampaignName("some name")
        googleAnalyticsSetting.setCampaignMedium("some medium")
        trackingSettings.setGoogleAnalyticsSetting(googleAnalyticsSetting)
        mail.setTrackingSettings(trackingSettings)

        val replyTo = Email()
        replyTo.setName("Example User")
        replyTo.setEmail("test@example.com")
        mail.setReplyTo(replyTo)

        return mail
    }

    // Minimum required to send an email
    @Throws(IOException::class)
    fun buildHelloEmail(): Mail {
        val from = Email("test@example.com")
        val subject = "Hello World from the SendGrid Java Library"
        val to = Email("test@example.com")
        val content = Content("text/plain", "some text here")
        // Note that when you use this constructor an initial personalization object
        // is created for you. It can be accessed via
        // mail.personalization.get(0) as it is a List object
        val mail = Mail(from, subject, to, content)
        val email = Email("test2@example.com")
        mail.personalization.get(0).addTo(email)

        return mail
    }

    @Throws(IOException::class)
    fun baselineExample() {
        val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
        sg.addRequestHeader("X-Mock", "true")

        val request = Request()
        val helloWorld = buildHelloEmail()
        try {
            request.setMethod(Method.POST)
            request.setEndpoint("mail/send")
            request.setBody(helloWorld.build())
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }

    @Throws(IOException::class)
    fun kitchenSinkExample() {
        val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
        sg.addRequestHeader("X-Mock", "true")

        val request = Request()
        val kitchenSink = buildKitchenSink()
        try {
            request.setMethod(Method.POST)
            request.setEndpoint("mail/send")
            request.setBody(kitchenSink.build())
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }

    @Throws(IOException::class)
    fun main(args: Array<String>) {
        baselineExample()
        kitchenSinkExample()
    }
}