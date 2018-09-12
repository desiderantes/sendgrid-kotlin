import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

//////////////////////////////////////////////////////////////////
// Retrieve all categories
// GET /categories


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("categories")
            request.addQueryParam("category", "test_string")
            request.addQueryParam("limit", "1")
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

//////////////////////////////////////////////////////////////////
// Retrieve Email Statistics for Categories
// GET /categories/stats


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("categories/stats")
            request.addQueryParam("end_date", "2016-04-01")
            request.addQueryParam("aggregated_by", "day")
            request.addQueryParam("limit", "1")
            request.addQueryParam("offset", "1")
            request.addQueryParam("start_date", "2016-01-01")
            request.addQueryParam("categories", "test_string")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}

//////////////////////////////////////////////////////////////////
// Retrieve sums of email stats for each category [Needs: Stats object defined, has category ID?]
// GET /categories/stats/sums


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("categories/stats/sums")
            request.addQueryParam("end_date", "2016-04-01")
            request.addQueryParam("aggregated_by", "day")
            request.addQueryParam("limit", "1")
            request.addQueryParam("sort_by_metric", "test_string")
            request.addQueryParam("offset", "1")
            request.addQueryParam("start_date", "2016-01-01")
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

