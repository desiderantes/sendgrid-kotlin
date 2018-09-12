import java.io.IOException

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid

//////////////////////////////////////////////////////////////////
// Update plain content mail settings
// PATCH /mail_settings/plain_content


object UpdatePlainContentMailSettings {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.PATCH)
            request.setEndpoint("mail_settings/plain_content")
            request.setBody("{\"enabled\":false}")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}