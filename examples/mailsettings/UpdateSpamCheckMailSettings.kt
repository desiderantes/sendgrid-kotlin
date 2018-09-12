import java.io.IOException

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid

//////////////////////////////////////////////////////////////////
// Update spam check mail settings
// PATCH /mail_settings/spam_check


object UpdateSpamCheckMailSettings {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.PATCH)
            request.setEndpoint("mail_settings/spam_check")
            request.setBody("{\"url\":\"url\",\"max_score\":5,\"enabled\":true}")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}