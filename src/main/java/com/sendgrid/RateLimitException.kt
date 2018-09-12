package com.sendgrid

/**
 * An exception thrown when the maximum number of retries
 * have occurred, and the API calls are still rate limited.
 */
class RateLimitException
/**
 * Construct a new exception.
 * @param request the originating request object.
 * @param retryCount the number of times a retry was attempted.
 */
constructor(val request: Request, val retryCount: Int) : Exception("Rate limit for request $request : Try again in " +
        "$retryCount")
