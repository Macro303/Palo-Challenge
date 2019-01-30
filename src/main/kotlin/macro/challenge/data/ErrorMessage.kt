package macro.challenge.data

import io.ktor.http.HttpStatusCode

/**
 * Created by Macro303 on 2019-Jan-30.
 */
data class ErrorMessage(
	val request: String,
	val message: String,
	val code: HttpStatusCode,
	val cause: Throwable? = null
)