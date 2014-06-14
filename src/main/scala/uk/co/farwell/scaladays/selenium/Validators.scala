package uk.co.farwell.scaladays.selenium

case class Validation(condition: Boolean, message: String)

class UserValidator(dao: UserDao) {
  private def validation(messages: List[String]): Validation = {
    if (messages.isEmpty) Validation(true, "ok") else Validation(false, messages.mkString("\n"))
  }

  def validateUpdate(id: Int, t: UsersRow): Validation = validation(update(id, t).flatten)
  def validateCreate(t: UsersRow): Validation = validation(create(t).flatten)

  private def create(u: UsersRow): List[Option[String]] = {
    validate(nonEmpty(u.password), "password should be specified") ::
      validate(!exists(u.username), "user with specified username already exists") ::
      common(u)
  }

  private def exists(uo: Option[String]) = {
    uo.flatMap(un => dao.list().find(u => u.username == Some(un))).isDefined
  }

  private def update(id: Int, u: UsersRow): List[Option[String]] = {
    val user = dao.get(id)

    validate(user.isDefined, "user with specified id does not exist") ::
      validate((user.isEmpty || user.get.username == u.username), "cannot change username") ::
      common(u)
  }

  private def nonEmpty(d: Option[String]) = (d.isDefined && d.get.trim() != "")

  private def common(u: UsersRow): List[Option[String]] = {
    List(
      validate(u.id >= 0, "incorrect user id"),
      validate(nonEmpty(u.username), "username should be specified"),
      validate(nonEmpty(u.fullName), "full name should be specified"))
  }

  private def validate(b: => Boolean, message: String) = if (!b) Some(message) else None
}
