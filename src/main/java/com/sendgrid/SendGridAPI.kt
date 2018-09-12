package com.sendgrid

import java.io.IOException

interface SendGridAPI {

    val libraryVersion: String

    var version: String

    val requestHeaders: Map<String, String>

    var host: String

    /**
     * Adds a request headers.
     *
     * @param key the key
     * @param value the value
     * @return returns a map of request headers.
     */
    fun addRequestHeader(key: String, value: String): Map<String, String>

    /**
     * Removes a request headers.
     *
     * @param key the key
     * @return returns a map of request headers.
     */
    fun removeRequestHeader(key: String): Map<String, String>

    /**
     * Class makeCall makes the call to the SendGrid API, override this method for
     * testing.
     *
     * @param request the request
     * @return returns a response.
     * @throws IOException in case of network or marshal error.
     */
    @Throws(IOException::class)
    fun makeCall(request: Request): Response

    /**
     * Class api sets up the request to the SendGrid API, this is main interface.
     *
     * @param request the request
     * @return returns a response.
     * @throws IOException in case of network or marshal error.
     */
    @Throws(IOException::class)
    fun api(request: Request): Response
}
