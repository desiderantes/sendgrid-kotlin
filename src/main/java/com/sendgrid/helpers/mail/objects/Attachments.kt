package com.sendgrid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.codec.binary.Base64
import java.io.InputStream

/**
 * An attachment object.
 */
@JsonInclude(Include.NON_DEFAULT)
data class Attachments @JvmOverloads constructor(

        /** The attachment content.  */
        @JsonProperty("content")
        @get:JsonProperty("content")
        var content: String,

        /**
         * The mime type of the content you are attaching. For example,
         * “text/plain” or “text/html”.
         */
        @JsonProperty("type")
        @get:JsonProperty("type")
        var type: String? = null,

        /** The attachment file name.  */
        @JsonProperty("filename")
        @get:JsonProperty("filename")
        var filename: String,

        /**
         * The content-disposition of the attachment specifying
         * how you would like the attachment to be displayed.
         * For example, “inline” results in the attached file
         * being displayed automatically within the message
         * while “attachment” results in the attached file
         * requiring some action to be taken before it is
         * displayed (e.g. opening or downloading the file).
         */
        @JsonProperty("disposition")
        @get:JsonProperty("disposition")
        var disposition: String? = null,

        /**
         * The attachment content ID. This is used when the
         * disposition is set to “inline” and the attachment
         * is an image, allowing the file to be displayed within
         * the body of your email.
         */
        @JsonProperty("content_id")
        @get:JsonProperty("content_id")
        var contentId: String? = null
) {
    constructor(fileName: String, contentStream: InputStream) : this(filename = fileName, content = encodeToBase64(contentStream))

    companion object {
        private fun encodeToBase64(content: InputStream): String {
            return Base64.encodeBase64String(content.readBytes())
        }
    }
}
