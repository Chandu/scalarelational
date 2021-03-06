package org.scalarelational.h2

import org.scalarelational.Session
import org.scalarelational.column.property.{AutoIncrement, PrimaryKey, Unique}
import org.scalarelational.table.Table
import org.scalatest.{Matchers, WordSpec}

class SessionSpec extends WordSpec with Matchers {
  "Session" when {
    "not sticky" should {
      import SessionDatastore._

      "not yet exist" in {
        hasSession should equal(false)
      }
      "properly create and release" in {
        withSession { implicit session =>
          hasSession should equal(true)
        }
        hasSession should equal(false)
      }
      "wrap session calls for a single session" in {
        withSession { implicit session =>
          val s = session
          withSession { implicit session =>
            s should be theSameInstanceAs session
          }
        }
      }
      "create distinct sessions" in {
        var s1: Session = null
        var s2: Session = null
        withSession { implicit session =>
          s1 = session
        }
        withSession { implicit session =>
          s2 = session
        }
        s1.disposed should equal(true)
        s2.disposed should equal(true)
        s1 should not be theSameInstanceAs(s2)
      }
    }
  }
}

object SessionDatastore extends H2Datastore {
  object fruit extends Table("FRUIT") {
    val name = column[String]("name", Unique)
    val id = column[Int]("id", PrimaryKey, AutoIncrement)
  }
}