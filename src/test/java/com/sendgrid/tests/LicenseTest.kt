package com.sendgrid.tests

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.io.BufferedReader
import java.io.FileReader
import java.time.LocalDate
import kotlin.test.assertEquals

class LicenseTest : Spek({

    given("our license file") {
        it("should have the correct year") {
            var copyrightText: String? = null
            BufferedReader(FileReader("./LICENSE.md")).use { br ->
                br.useLines { copyrightText = it.find { that -> that.startsWith("Copyright") } }
            }
            val expectedCopyright = String.format("Copyright (c) 2013-%d SendGrid, Inc.", LocalDate.now().year)
            assertEquals(copyrightText, expectedCopyright, "License has incorrect year")
        }
    }
})
