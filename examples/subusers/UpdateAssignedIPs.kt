import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

//////////////////////////////////////////////////////////////////
// Update IPs assigned to a subuser
// PUT /subusers/{subuser_name}/ips


object UpdateAssignedIps {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.PUT)
            request.setEndpoint("subusers/{subuser_name}/ips")
            request.setBody("[\"127.0.0.1\"]")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}