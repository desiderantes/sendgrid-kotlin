import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

//////////////////////////////////////////////////////////////////
// Retrieve email statistics by browser.
// GET /browsers/stats


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("browsers/stats")
            request.addQueryParam("end_date", "2016-04-01")
            request.addQueryParam("aggregated_by", "day")
            request.addQueryParam("browsers", "test_string")
            request.addQueryParam("limit", "test_string")
            request.addQueryParam("offset", "test_string")
            request.addQueryParam("start_date", "2016-01-01")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}

