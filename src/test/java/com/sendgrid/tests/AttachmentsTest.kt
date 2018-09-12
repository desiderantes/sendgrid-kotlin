package com.sendgrid.tests

import com.sendgrid.Attachments
import org.apache.commons.codec.binary.Base64
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class AttachmentsTest : Spek({

    describe("Attachments") {

        it(" can be created ") {
            val fileName = "book.txt"
            val type = "text/plain"
            val content = "This test checks if the builder works fine"
            val contentStream = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8))
            val contentId = "someId"
            val dispositon = "someDisposition"

            val (content1, type1, filename, disposition, contentId1) = Attachments(fileName, contentStream).apply {
                this.type = type
                this.contentId = contentId
                this.disposition = dispositon
            }

            assertEquals(type1, type)
            assertEquals(filename, fileName)
            assertEquals(contentId1, contentId)
            assertEquals(disposition, dispositon)
            assertEquals(content1, Base64.encodeBase64String(content.toByteArray(Charsets.UTF_8)))
        }
    }
})
