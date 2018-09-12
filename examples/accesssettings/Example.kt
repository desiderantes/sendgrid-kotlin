import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid

import java.io.IOException

class Example {

    protected fun createRequest(method: Method, endPoint: String, requestBody: String?): Request {
        val request = Request()
        request.setMethod(method)
        request.setEndpoint(endPoint)
        if (requestBody != null) {
            request.setBody(requestBody)
        }
        return request
    }

    @Throws(IOException::class)
    protected fun execute(request: Request): Response {
        val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
        return sg.api(request)
    }

    protected fun printResonseInfo(response: Response) {
        System.out.println(response.getStatusCode())
        System.out.println(response.getBody())
        System.out.println(response.getHeaders())
    }
}