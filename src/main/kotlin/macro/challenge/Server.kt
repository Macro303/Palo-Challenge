package macro.challenge

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import macro.challenge.Server.LOGGER
import macro.challenge.core.Util
import macro.challenge.data.ErrorMessage
import macro.challenge.data.PersonController.personRoutes
import org.apache.logging.log4j.LogManager

object Server {
	internal val LOGGER = LogManager.getLogger(Server::class.java)

	init {
		LOGGER.info("Initializing Challenge")
		loggerColours()
	}

	private fun loggerColours() {
		LOGGER.trace("TRACE is Visible")
		LOGGER.debug("DEBUG is Visible")
		LOGGER.info("INFO is Visible")
		LOGGER.warn("WARN is Visible")
		LOGGER.error("ERROR is Visible")
		LOGGER.fatal("FATAL is Visible")
	}

	@JvmStatic
	fun main(args: Array<String>) {
		embeddedServer(
			Netty,
			port = 7077,
			host = "localhost",
			module = Application::module
		).apply { start(wait = true) }
	}
}

fun Application.module() {
	install(ContentNegotiation) {
		register(contentType = ContentType.Application.Json, converter = GsonConverter(gson = Util.GSON))
	}
	install(DefaultHeaders) {
		header(name = HttpHeaders.Server, value = "Ktor-Challenge")
		header(name = "Developer", value = "Macro303")
		header(name = HttpHeaders.ContentLanguage, value = "en-NZ")
	}
	install(Compression)
	install(ConditionalHeaders)
	install(AutoHeadResponse)
	install(StatusPages) {
		exception<Throwable> {
			val error = ErrorMessage(
				code = HttpStatusCode.InternalServerError,
				request = call.request.local.uri,
				message = it.toString(),
				cause = it
			)
			LOGGER.error(error.message, error.cause)
			call.respond(status = error.code, message = error)
		}
	}
	intercept(ApplicationCallPipeline.Setup) {
		LOGGER.debug(">> ${call.request.httpVersion} ${call.request.httpMethod.value} ${call.request.uri}, Content-Type: ${call.request.contentType()}, User-Agent: ${call.request.userAgent()}, Host: ${call.request.host()}:${call.request.port()}")
	}
	intercept(ApplicationCallPipeline.Fallback) {
		LOGGER.info("${call.response.status()} << >> ${call.request.httpVersion} ${call.request.httpMethod.value} ${call.request.path()}, Content-Type: ${call.request.contentType()}")
		LOGGER.debug("${call.response.status()} << ${call.request.path()}, Content-Type: ${call.response.headers["Content-Type"]}")
	}
	install(Routing) {
		personRoutes()
		static {
			defaultResource(resource = "static/index.html")
			resource(remotePath = "/styles.css", resource = "static/css/styles.css")
			resource(remotePath = "/script.js", resource = "static/js/script.js")
		}
	}
}