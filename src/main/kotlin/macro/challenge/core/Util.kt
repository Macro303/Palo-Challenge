package macro.challenge.core

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

/**
 * Created by Macro303 on 2019-Jan-30.
 */
object Util {
	private val LOGGER = LogManager.getLogger(Util::class.java)
	internal val GSON = GsonBuilder()
		.setPrettyPrinting()
		.serializeNulls()
		.disableHtmlEscaping()
		.create()
	private val database = Database.connect(url = "jdbc:sqlite:Challenge.db", driver = "org.sqlite.JDBC")

	internal fun <T> query(block: () -> T): T {
		return transaction(
			transactionIsolation = Connection.TRANSACTION_SERIALIZABLE,
			repetitionAttempts = 1,
			db = database
		) {
			addLogger(Slf4jSqlDebugLogger)
			block()
		}
	}

	@Throws(JsonSyntaxException::class)
	internal fun String.fromJSON(): Map<String, Any> {
		if (this.isBlank()) return emptyMap()
		return try {
			val typeOfHashMap = object : TypeToken<Map<String, Any>>() {
			}.type
			GSON.fromJson(this, typeOfHashMap)
		} catch (jse: JsonSyntaxException) {
			LOGGER.error("Unable to parse string: $this", jse)
			emptyMap()
		}
	}
}