import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

//////////////////////////////////////////////////////////////////
// Retrieve monthly stats for all subusers
// GET /subusers/stats/monthly


object RetrieveMonthlyStats {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("subusers/stats/monthly")
            request.addQueryParam("subuser", "test_string")
            request.addQueryParam("limit", "1")
            request.addQueryParam("sort_by_metric", "test_string")
            request.addQueryParam("offset", "1")
            request.addQueryParam("date", "test_string")
            request.addQueryParam("sort_by_direction", "asc")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}
