package util


/**
 * Helpful for handling network responses. Very Helpful
 * for handling successful and error responses,
 * also for loading state like response is processing
 * or when answer is received. It is mostly used in handling network requests but it is useful for
 * handling non-network request as well eg. database operation.
 *
 */
sealed class Resource<T>(
    /**
     * "data" - represents the body after successful response eg.data from a successful network request. It is
     * nullable because we may not get the desired response and get a error instead.
     * "message" - it could be the error message. It is nullable in case we don't get any error (or any kind of)
     * message from the network request
     */
    val data: T? = null,
    val message:String? = null
) {
    /**
     * The constructor variable "data" is not nullable
     * because if the response is successful we are sure that
     * we have data i.e. our response has body.
     */
    class Success<T>(data: T): Resource<T>(data)

    /**
     * Error class has "data" as constructor variable because we may sometime get data with the error message
     */
    class Error<T>(message: String, data: T?=null): Resource<T>(data, message)

    class Loading<T>: Resource<T>()
}