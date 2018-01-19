package co.upvest.mberndt

import scala.util.Try

sealed trait Message
case class Coordinates(lat: Double, long: Double, description: String) extends Message
case class Greeting(number: Int) extends Message

object Message {
  // might migrate this to parser combinators some time, but for now, some regexes will do.
  private val coordinatesRegex = """Coordinates\(([^,]+),([^,]+),([^)]+)\)""".r
  private val greetingRegex = """Greets\(Privet: \{([0-9]+)\}\)""".r
  private class extract[A](f: String => A) {
    def unapply(s: String): Option[A] = Try(f(s)).toOption
  }
  private object extractDouble extends extract(_.toDouble)
  private object extractInt extends extract(_.toInt)


  def parse(s: String): Option[Message] = Some(s) collect {
    case coordinatesRegex(extractDouble(lat), extractDouble(long), description) =>
      Coordinates(lat, long, description)
    case greetingRegex(extractInt(num)) =>
      Greeting(num)
  }
}
