package com.sendgrid.tests

import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import java.io.IOException
import java.util.*

class MockSendGrid(apiKey: String) : SendGrid(apiKey) {

    @Throws(IOException::class)
    override fun makeCall(request: Request): Response {
        val response = Response()
        response.statusCode = 200
        response.body = "{\"message\":\"success\"}"
        val headers = HashMap<String, String>()
        headers["Test"] = "Header"
        response.headers = headers
        return response
    }
}