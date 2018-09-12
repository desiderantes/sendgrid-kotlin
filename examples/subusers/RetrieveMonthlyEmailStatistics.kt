import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap


//////////////////////////////////////////////////////////////////
// Retrieve the monthly email statistics for a single subuser
// GET /subusers/{subuser_name}/stats/monthly


object RetrieveMonthlyEmailStatistics {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("subusers/{subuser_name}/stats/monthly")
            request.addQueryParam("date", "test_string")
            request.addQueryParam("sort_by_direction", "asc")
            request.addQueryParam("limit", "1")
            request.addQueryParam("sort_by_metric", "test_string")
            request.addQueryParam("offset", "1")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}