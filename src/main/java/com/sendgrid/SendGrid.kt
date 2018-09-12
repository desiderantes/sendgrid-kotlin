package com.sendgrid

import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Class SendGrid allows for quick and easy access to the SendGrid API.
 */
open class SendGrid : SendGridAPI {

    private var pool: ExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

    /** The user's SendGrid API Key: https://app.sendgrid.com/settings/api_keys.  */
    private var apiKey: String

    /** The SendGrid host to which to connect. (api.sendgrid.com by default). */

    override var host: String = "api.sendgrid.com"

    /**
     * The API version (v3 by default).
     */
    override var version: String = "v3"

    /** The HTTP client.  */
    private var client: Client

    /** The request headers container.  */
    private var headers: MutableMap<String, String>

    override val requestHeaders: MutableMap<String, String>
        get() = headers
    /** The number of times to try after a rate limit.  */
    /**
     * Get the maximum number of retries on a rate limit response.
     * The default is 5.
     * @return the number of retries on a rate limit.
     */
    /**
     * Set the maximum number of retries on a rate limit response.
     * @param rateLimitRetry the maximum retry count.
     */
    var rateLimitRetry: Int = 5

    /** The number of milliseconds to sleep between retries.  */
    /**
     * Get the duration of time (in milliseconds) to sleep between
     * consecutive rate limit retries. The SendGrid API enforces
     * the rate limit to the second. The default value is 1.1 seconds.
     * @return the sleep duration.
     */
    /**
     * Set the duration of time (in milliseconds) to sleep between
     * consecutive rate limit retries.
     * @param rateLimitSleep the sleep duration.
     */
    var rateLimitSleep: Int = 1100

    /**
     * Retrieve the current library version.
     * @return the current version.
     */
    override val libraryVersion: String
        get() = VERSION

    /**
     * Construct a new SendGrid API wrapper.
     * @param apiKey is your SendGrid API Key: https://app.sendgrid.com/settings/api_keys
     */
    constructor(apiKey: String) {
        this.apiKey = apiKey
        this.client = Client()
        this.headers = mutableMapOf(
                "Authorization" to "Bearer $apiKey",
                "User-agent" to USER_AGENT,
                "Accept" to "application/json"
        )
    }

    /**
     * Construct a new SendGrid API wrapper.
     * @param apiKey is your SendGrid API Key: https://app.sendgrid.com/settings/api_keys
     * @param test is true if you are unit testing
     */
    constructor(apiKey: String, test: Boolean?) {
        this.apiKey = apiKey
        this.client = Client(test)
        this.headers = mutableMapOf(
                "Authorization" to "Bearer $apiKey",
                "User-agent" to USER_AGENT,
                "Accept" to "application/json"
        )
    }

    /**
     * Construct a new SendGrid API wrapper.
     * @param apiKey is your SendGrid API Key: https://app.sendgrid.com/settings/api_keys
     * @param client the Client to use (allows to customize its configuration)
     */
    constructor(apiKey: String, client: Client) {
        this.apiKey = apiKey
        this.client = client
        this.headers = mutableMapOf(
                "Authorization" to "Bearer $apiKey",
                "User-agent" to USER_AGENT,
                "Accept" to "application/json"
        )
    }

    /**
     * Add a new request header.
     * @param key the header key.
     * @param value the header value.
     * @return the new set of request headers.
     */
    override fun addRequestHeader(key: String, value: String): Map<String, String> {
        this.headers[key] = value
        return requestHeaders
    }

    /**
     * Remove a request header.
     * @param key the header key to remove.
     * @return the new set of request headers.
     */
    override fun removeRequestHeader(key: String): Map<String, String> {
        this.headers.remove(key)
        return requestHeaders
    }

    /**
     * Makes the call to the SendGrid API, override this method for testing.
     * @param request the request to make.
     * @return the response object.
     * @throws IOException in case of a network error.
     */
    @Throws(IOException::class)
    override fun makeCall(request: Request): Response {
        return client.api(request)
    }

    /**
     * Class api sets up the request to the SendGrid API, this is main interface.
     * @param request the request object.
     * @return the response object.
     * @throws IOException in case of a network error.
     */
    @Throws(IOException::class)
    override fun api(request: Request): Response {
        val req = Request().apply {
            this.method = request.method
            this.baseUri = host
            this.endpoint = "/$version/${request.endpoint}"
            this.body = request.body
            for ((key, value) in this.headers) {
                this.addHeader(key, value)
            }
            for ((key, value) in request.queryParams) {
                this.addQueryParam(key, value)
            }
        }

        return makeCall(req)
    }

    /**
     * Attempt an API call. This method executes the API call asynchronously
     * on an internal thread pool. If the call is rate limited, the thread
     * will retry up to the maximum configured time.
     * @param request the API request.
     */
    fun attempt(request: Request) {
        this.attempt(request, onError = { ex -> }, onResponse = { r: Response -> })
    }

    /**
     * Attempt an API call. This method executes the API call asynchronously
     * on an internal thread pool. If the call is rate limited, the thread
     * will retry up to the maximum configured time. The supplied callback
     * will be called in the event of an error, or a successful response.
     * @param request the API request.
     * @param onError the error callback.
     * @param onResponse the response callback
     */
    fun attempt(request: Request, onError: (error: Exception) -> Unit, onResponse: (response: Response) -> Unit) {

        this.pool.execute {
            var response: Response

            // Retry until the retry limit has been reached.
            for (i in 0 until rateLimitRetry) {
                try {
                    response = api(request)
                } catch (ex: IOException) {
                    // Stop retrying if there is a network error.
                    onError(ex)
                    return@execute
                }

                // We have been rate limited.
                if (response.statusCode == RATE_LIMIT_RESPONSE_CODE) {
                    try {
                        Thread.sleep(rateLimitSleep.toLong())
                    } catch (ex: InterruptedException) {
                        // Can safely ignore this exception and retry.
                    }

                } else {
                    onResponse(response)
                    return@execute
                }
            }

            // Retries exhausted. Return error.
            onError(RateLimitException(request, rateLimitRetry))
        }
    }

    /**
     * Attempt an API call. This method executes the API call asynchronously
     * on an internal thread pool. If the call is rate limited, the thread
     * will retry up to the maximum configured time. The supplied callback
     * will be called in the event of an error, or a successful response.
     * @param request the API request.
     * @param callback the callback.
     */
    fun attempt(request: Request, callback: APICallback) {
        this.attempt(request, onError = { err: Exception -> callback.error(err) }, onResponse = { response -> callback.response(response) })
    }

    companion object {

        private val VERSION = "3.0.0"

        /** The user agent string to return to SendGrid.  */
        private val USER_AGENT = "sendgrid/$VERSION;kotlin"
        private val RATE_LIMIT_RESPONSE_CODE = 429
        private val THREAD_POOL_SIZE = 8
    }
}
