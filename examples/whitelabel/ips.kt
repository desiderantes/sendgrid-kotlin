import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

class CommonExample {
    protected var sg: SendGrid
    protected var request: Request

    companion object {

        protected fun init() {
            this.sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            this.request = Request()
        }
    }
}

//////////////////////////////////////////////////////////////////
// Create an IP whitelabel
// POST /whitelabel/ips


class Example : CommonExample() {
    companion object {
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            try {
                CommonExample.init()
                request.setMethod(Method.POST)
                request.setEndpoint("whitelabel/ips")
                request.setBody("{\"ip\":\"192.168.1.1\",\"domain\":\"example.com\",\"subdomain\":\"email\"}")
                val response = sg.api(request)
                System.out.println(response.getStatusCode())
                System.out.println(response.getBody())
                System.out.println(response.getHeaders())
            } catch (ex: IOException) {
                throw ex
            }

        }
    }
}

//////////////////////////////////////////////////////////////////
// Retrieve all IP whitelabels
// GET /whitelabel/ips


class Example : CommonExample() {
    companion object {
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            try {
                CommonExample.init()
                request.setMethod(Method.GET)
                request.setEndpoint("whitelabel/ips")
                request.addQueryParam("ip", "test_string")
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
}

//////////////////////////////////////////////////////////////////
// Retrieve an IP whitelabel
// GET /whitelabel/ips/{id}


class Example : CommonExample() {
    companion object {
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            try {
                CommonExample.init()
                request.setMethod(Method.GET)
                request.setEndpoint("whitelabel/ips/{id}")
                val response = sg.api(request)
                System.out.println(response.getStatusCode())
                System.out.println(response.getBody())
                System.out.println(response.getHeaders())
            } catch (ex: IOException) {
                throw ex
            }

        }
    }
}

//////////////////////////////////////////////////////////////////
// Delete an IP whitelabel
// DELETE /whitelabel/ips/{id}


class Example : CommonExample() {
    companion object {
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            try {
                CommonExample.init()
                request.setMethod(Method.DELETE)
                request.setEndpoint("whitelabel/ips/{id}")
                val response = sg.api(request)
                System.out.println(response.getStatusCode())
                System.out.println(response.getBody())
                System.out.println(response.getHeaders())
            } catch (ex: IOException) {
                throw ex
            }

        }
    }
}

//////////////////////////////////////////////////////////////////
// Validate an IP whitelabel
// POST /whitelabel/ips/{id}/validate


class Example : CommonExample() {
    companion object {
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            try {
                CommonExample.init()
                request.setMethod(Method.POST)
                request.setEndpoint("whitelabel/ips/{id}/validate")
                val response = sg.api(request)
                System.out.println(response.getStatusCode())
                System.out.println(response.getBody())
                System.out.println(response.getHeaders())
            } catch (ex: IOException) {
                throw ex
            }

        }
    }
}
