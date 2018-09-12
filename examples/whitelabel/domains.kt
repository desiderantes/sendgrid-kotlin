import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import com.sendgrid.*

import java.io.IOException
import java.util.HashMap

//////////////////////////////////////////////////////////////////
// Create a domain whitelabel.
// POST /whitelabel/domains


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.POST)
            request.setEndpoint("whitelabel/domains")
            request.setBody("{\"automatic_security\":false,\"username\":\"john@example.com\",\"domain\":\"example.com\",\"default\":true,\"custom_spf\":true,\"ips\":[\"192.168.1.1\",\"192.168.1.2\"],\"subdomain\":\"news\"}")
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
// List all domain whitelabels.
// GET /whitelabel/domains


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("whitelabel/domains")
            request.addQueryParam("username", "test_string")
            request.addQueryParam("domain", "test_string")
            request.addQueryParam("exclude_subusers", "true")
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
// Get the default domain whitelabel.
// GET /whitelabel/domains/default


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("whitelabel/domains/default")
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
// List the domain whitelabel associated with the given user.
// GET /whitelabel/domains/subuser


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("whitelabel/domains/subuser")
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
// Disassociate a domain whitelabel from a given user.
// DELETE /whitelabel/domains/subuser


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.DELETE)
            request.setEndpoint("whitelabel/domains/subuser")
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
// Update a domain whitelabel.
// PATCH /whitelabel/domains/{domain_id}


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.PATCH)
            request.setEndpoint("whitelabel/domains/{domain_id}")
            request.setBody("{\"default\":false,\"custom_spf\":true}")
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
// Retrieve a domain whitelabel.
// GET /whitelabel/domains/{domain_id}


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.GET)
            request.setEndpoint("whitelabel/domains/{domain_id}")
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
// Delete a domain whitelabel.
// DELETE /whitelabel/domains/{domain_id}


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.DELETE)
            request.setEndpoint("whitelabel/domains/{domain_id}")
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
// Associate a domain whitelabel with a given user.
// POST /whitelabel/domains/{domain_id}/subuser


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.POST)
            request.setEndpoint("whitelabel/domains/{domain_id}/subuser")
            request.setBody("{\"username\":\"jane@example.com\"}")
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
// Add an IP to a domain whitelabel.
// POST /whitelabel/domains/{id}/ips


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.POST)
            request.setEndpoint("whitelabel/domains/{id}/ips")
            request.setBody("{\"ip\":\"192.168.0.1\"}")
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
// Remove an IP from a domain whitelabel.
// DELETE /whitelabel/domains/{id}/ips/{ip}


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.DELETE)
            request.setEndpoint("whitelabel/domains/{id}/ips/{ip}")
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
// Validate a domain whitelabel.
// POST /whitelabel/domains/{id}/validate


object Example {
    @Throws(IOException::class)
    fun main(args: Array<String>) {
        try {
            val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
            val request = Request()
            request.setMethod(Method.POST)
            request.setEndpoint("whitelabel/domains/{id}/validate")
            val response = sg.api(request)
            System.out.println(response.getStatusCode())
            System.out.println(response.getBody())
            System.out.println(response.getHeaders())
        } catch (ex: IOException) {
            throw ex
        }

    }
}
