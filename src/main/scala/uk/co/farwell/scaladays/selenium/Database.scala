package uk.co.farwell.scaladays.selenium

import scala.collection.concurrent.TrieMap

case class UsersRow(id: Long, username: Option[String], password: Option[String], fullName: Option[String], role: Option[String])

class UserDao() {
  val initial = List(
    UsersRow(1, Some("admin"), Some("admin"), Some("Administrator"), Some("admin")),
    UsersRow(2, Some("matthew"), Some("matthew"), Some("Matthew Farwell"), Some("normal"))
    ) ::: (1 to 130).map(i => UsersRow(i + 10, Some("user" + i), Some("user" + i), Some("user " + i), Some("normal"))).toList

    val map = TrieMap[Long, UsersRow]()

  map ++= initial.map(i => (i.id, i))
  var baseId: Long = 10000

  def list(): List[UsersRow] = map.map(_._2).toList
  def get(id: Long): Option[UsersRow] = map.get(id)
  def create(e: UsersRow): Option[UsersRow] = { baseId = baseId + 1; val newE = e.copy(id = baseId); map.put(baseId, newE); map.get(baseId) }
  def update(id: Long, e: UsersRow): Option[UsersRow] = { map.put(id, e); map.get(id) }
}
