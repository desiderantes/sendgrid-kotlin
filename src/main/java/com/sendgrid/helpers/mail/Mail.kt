package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.IOException

/**
 * Class Mail builds an object that sends an email through SendGrid.
 * Note that this object is not thread safe.
 */
@JsonInclude(Include.NON_DEFAULT)
data class Mail @JvmOverloads constructor(

        /** The email's from field.  */
        @JsonProperty("from")
        var from: Email? = null,

        /** The email's subject line. This is the global, or
         * “message level”, subject of your email. This may
         * be overridden by personalizations[x].subject.
         */
        @JsonProperty("subject")
        var subject: String? = null,

        /**
         * The email's personalization. Each object within
         * personalizations can be thought of as an envelope
         * - it defines who should receive an individual message
         * and how that message should be handled.
         */
        @JsonProperty("personalizations")
        var personalization: MutableList<Personalization> = mutableListOf(),

        /** The email's content.  */
        @JsonProperty("content")
        var content: MutableList<Content> = mutableListOf(),

        /** The email's attachments.  */
        @JsonProperty("attachments")
        var attachments: MutableList<Attachments> = mutableListOf(),

        /** The email's template ID.  */
        @JsonProperty("template_id")
        var templateId: String? = null,

        /**
         * The email's sections. An object of key/value pairs that
         * define block sections of code to be used as substitutions.
         */
        @JsonProperty("sections")
        var sections: MutableMap<String, String> = mutableMapOf(),

        /** The email's headers.  */
        @JsonProperty("headers")
        var headers: MutableMap<String, String> = mutableMapOf(),

        /** The email's categories.  */
        @JsonProperty("categories")
        var categories: MutableList<String> = mutableListOf(),

        /**
         * The email's custom arguments. Values that are specific to
         * the entire send that will be carried along with the email
         * and its activity data. Substitutions will not be made on
         * custom arguments, so any string that is entered into this
         * parameter will be assumed to be the custom argument that
         * you would like to be used. This parameter is overridden by
         * personalizations[x].custom_args if that parameter has been
         * defined. Total custom args size may not exceed 10,000 bytes.
         */
        @JsonProperty("custom_args")
        var customArgs: MutableMap<String, String> = mutableMapOf(),

        /**
         * A unix timestamp allowing you to specify when you want
         * your email to be delivered. This may be overridden by
         * the personalizations[x].send_at parameter. Scheduling
         * more than 72 hours in advance is forbidden.
         */
        @JsonProperty("send_at")
        var sendAt: Long = 0,

        /**
         * This ID represents a batch of emails to be sent at the
         * same time. Including a batch_id in your request allows
         * you include this email in that batch, and also enables
         * you to cancel or pause the delivery of that batch. For
         * more information, see https://sendgrid.com/docs/API_Reference/Web_API_v3/cancel_schedule_send.
         */
        @JsonProperty("batch_id")
        @get:JsonProperty("batch_id")
        var batchId: String? = null,

        /** The email's unsubscribe handling object.  */
        @JsonProperty("asm")
        @get:JsonProperty("asm")
        var asm: ASM? = null,

        /** The email's IP pool name.  */
        @JsonProperty("ip_pool_name")
        @get:JsonProperty("ip_pool_name")
        var ipPoolId: String? = null,

        /** The email's mail settings.  */
        @JsonProperty("mail_settings")
        @get:JsonProperty("mail_settings")
        var mailSettings: MailSettings? = null,

        /** The email's tracking settings.  */
        @JsonProperty("tracking_settings")
        @get:JsonProperty("tracking_settings")
        var trackingSettings: TrackingSettings? = null,

        /** The email's reply to address.  */
        @JsonProperty("reply_to")
        @get:JsonProperty("reply_to")
        var replyTo: Email? = null
) {

    /**
     * Construct a new Mail object.
     * @param from the email's from address.
     * @param subject the email's subject line.
     * @param to the email's recipient.
     * @param content the email's content.
     */
    constructor(from: Email, subject: String, to: Email, content: Content) : this(from = from, subject = subject) {
        val personalization = Personalization()
        personalization.addTo(to)
        this.addPersonalization(personalization)
        this.addContent(content)
    }

    /**
     * Add a personalizaton to the email.
     * @param personalization a personalization.
     */
    fun addPersonalization(personalization: Personalization) {
        this.personalization.add(personalization.copy())
    }

    /**
     * Add content to this email.
     * @param content content to add to this email.
     */
    fun addContent(content: Content) {
        this.content.add(content.copy())
    }

    /**
     * Add attachments to the email.
     * @param attachments attachments to add.
     */
    fun addAttachments(attachments: Attachments) {
        this.attachments.add(attachments.copy())
    }

    /**
     * Add a section to the email.
     * @param key the section's key.
     * @param value the section's value.
     */
    fun addSection(key: String, value: String) {
        this.sections[key] = value
    }

    /**
     * Add a header to the email.
     * @param key the header's key.
     * @param value the header's value.
     */
    fun addHeader(key: String, value: String) {
        this.headers[key] = value
    }

    /**
     * Add a category to the email.
     * @param category the category.
     */
    fun addCategory(category: String) {
        this.categories.add(category)
    }

    /**
     * Add a custom argument to the email.
     * @param key argument's key.
     * @param value the argument's value.
     */
    fun addCustomArg(key: String, value: String) {
        this.customArgs[key] = value
    }

    /**
     * Create a string represenation of the Mail object JSON.
     * @return a JSON string.
     * @throws IOException in case of a JSON marshal error.
     */
    @Throws(IOException::class)
    fun build(): String = SORTED_MAPPER.writeValueAsString(this)

    /**
     * Create a string represenation of the Mail object JSON and pretty print it.
     * @return a pretty JSON string.
     * @throws IOException in case of a JSON marshal error.
     */
    @Throws(IOException::class)
    fun buildPretty(): String = SORTED_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this)

    companion object {

        private val SORTED_MAPPER = jacksonObjectMapper()

        init {
            SORTED_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        }
    }
}