import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

//////////////////////////////////////////////////////////////////
// Retrieve email statistics by country and state/province.
// GET /geo/stats


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("geo/stats")
            request.addQueryParam("end_date", "2016-04-01")
            request.addQueryParam("country", "US")
            request.addQueryParam("aggregated_by", "day")
            request.addQueryParam("limit", "1")
            request.addQueryParam("offset", "1")
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

