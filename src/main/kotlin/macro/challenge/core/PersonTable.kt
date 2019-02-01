package macro.challenge.core

import org.jetbrains.exposed.sql.*

/**
 * Created by Macro303 on 2019-Jan-30.
 */
object PersonTable : Table(name = "Person") {
	val firstNameCol: Column<String> = text(name = "firstName")
	val lastNameCol: Column<String> = text(name = "lastName")
	val zeroCountCol: Column<Int> = integer(name = "zeroCount")

	init {
		Util.query {
			uniqueIndex(firstNameCol, lastNameCol)
			SchemaUtils.create(this)
		}
	}

	fun select(firstName: String, lastName: String): Person? = Util.query {
		PersonTable.select {
			PersonTable.firstNameCol eq firstName and (PersonTable.lastNameCol eq lastName)
		}.map {
			Person(
				firstName = it[PersonTable.firstNameCol],
				lastName = it[PersonTable.lastNameCol],
				zeroCount = it[PersonTable.zeroCountCol]
			)
		}.firstOrNull()
	}

	fun insert(firstName: String, lastName: String): Person? = Util.query {
		val person = select(firstName = firstName, lastName = lastName)
		if (person == null) {
			PersonTable.insert {
				it[PersonTable.firstNameCol] = firstName
				it[PersonTable.lastNameCol] = lastName
				it[PersonTable.zeroCountCol] = Person.calcCount(text = "$firstName $lastName")
			}
			select(firstName = firstName, lastName = lastName)!!
		} else
			person
	}
}