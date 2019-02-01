package macro.challenge.data

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.contentType
import io.ktor.routing.post
import io.ktor.routing.route
import macro.challenge.core.PersonTable
import macro.challenge.core.Util.fromJSON
import org.apache.logging.log4j.LogManager

/**
 * Created by Macro303 on 2019-Jan-30.
 */
object PersonController {
	private val LOGGER = LogManager.getLogger(PersonController::class.java)

	suspend fun parseRequest(call: ApplicationCall): Map<String, Any?>? {
		val body = call.receiveText().fromJSON()
		LOGGER.debug("Body: $body")
		val name: String? = body["Name"] as String?
		when {
			body.isEmpty() -> call.respond(
				message = mapOf("Message" to "You must pass an object in the body of the request that has all the following fields: Name"),
				status = HttpStatusCode.BadRequest
			)
			name == null -> call.respond(
				message = mapOf("Message" to "You must pass an object in the body of the request that has the \"Name\" field"),
				status = HttpStatusCode.BadRequest
			)
			else -> return body
		}
		return null
	}

	fun Route.personRoutes() {
		route(path = "/person") {
			contentType(contentType = ContentType.Application.Json) {
				post {
					val request = parseRequest(call = call) ?: return@post
					val zeroCount = PersonTable.insert(
						name = request["Name"] as String
					)?.zeroCount ?: 0
					call.respond(
						message = mapOf("Count" to zeroCount),
						status = HttpStatusCode.OK
					)
				}
			}
		}
	}
}