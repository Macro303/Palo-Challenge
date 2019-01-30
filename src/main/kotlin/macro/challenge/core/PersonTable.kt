package macro.challenge.core

import org.jetbrains.exposed.sql.*

/**
 * Created by Macro303 on 2019-Jan-30.
 */
object PersonTable : Table(name = "Person") {
	val nameCol: Column<String> = text(name = "name").uniqueIndex()
	val zeroCountCol: Column<Int> = integer(name = "zeroCount")

	init {
		Util.query {
			SchemaUtils.create(this)
		}
	}

	fun select(name: String): Person? = Util.query {
		PersonTable.select {
			PersonTable.nameCol eq name
		}.map {
			Person(
				name = it[PersonTable.nameCol],
				zeroCount = it[PersonTable.zeroCountCol]
			)
		}.firstOrNull()
	}

	fun insert(name: String): Person? = Util.query {
		val person = select(name = name)
		if (person == null) {
			PersonTable.insert {
				it[PersonTable.nameCol] = name
				it[PersonTable.zeroCountCol] = Person.calcCount(text = name)
			}
			select(name = name)!!
		} else
			person
	}
}