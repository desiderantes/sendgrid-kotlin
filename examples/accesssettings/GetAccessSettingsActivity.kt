import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid

import java.io.IOException

/*Retrieve all recent access attempts
GET /access_settings/activity
*/

class GetAccessSettingsActivity : Example() {

    @Throws(IOException::class)
    private fun run() {
        try {
            val endPoint = "access_settings/whitelist"
            val body = "{\"ids\":[1,2,3]}"
            val request = createRequest(Method.DELETE, endPoint, body)
            request.addQueryParam("limit", "1")
            val response = execute(request)
            printResponseInfo(response)
        } catch (ex: IOException) {
            throw ex
        }

    }

    companion object {

        @Throws(IOException::class)
        fun main(args: Array<String>) {
            val getAccessSettingsActivity = GetAccessSettingsActivity()
            getAccessSettingsActivity.run()
        }
    }
}


