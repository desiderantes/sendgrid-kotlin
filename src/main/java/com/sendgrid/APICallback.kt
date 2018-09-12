package com.sendgrid

/**
 * An interface describing a callback mechanism for the
 * asynchronous, rate limit aware API connection.
 */
interface APICallback {
    /**
     * Callback method in case of an error.
     * @param ex the error that was thrown.
     */
    fun error(ex: Exception)

    /**
     * Callback method in case of a valid response.
     * @param response the valid response.
     */
    fun response(response: Response)
}
