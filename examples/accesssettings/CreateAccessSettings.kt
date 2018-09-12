import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid

import java.io.IOException

/*Add one or more IPs to the whitelist
POST /access_settings/whitelist
*/

class CreateAccessSettings : Example() {

    @Throws(IOException::class)
    private fun run() {
        try {
            val endPoint = "access_settings/whitelist"
            val body = "{\"ips\":[{\"ip\":\"192.168.1.1\"},{\"ip\":\"192.*.*.*\"},{\"ip\":\"192.168.1.3/32\"}]}"
            val request = createRequest(Method.POST, endPoint, body)
            val response = execute(request)
            printResponseInfo(response)
        } catch (ex: IOException) {
            throw ex
        }

    }

    companion object {

        @Throws(IOException::class)
        fun main(args: Array<String>) {
            val createAccessSettings = CreateAccessSettings()
            createAccessSettings.run()
        }
    }
}
