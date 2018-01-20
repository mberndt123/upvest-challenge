package co.upvest.mberndt

import fastparse.all._
import ParseUtils.{int, double}

sealed trait Message
case class Coordinates(lat: Double, long: Double, description: String) extends Message
case class Greeting(number: Int) extends Message

object Message {
  private val greeting = P("Greets(Privet: {" ~ int.map(Greeting.apply) ~ "})")
  private val coordinates = P {
    ("Coordinates(" ~ double ~ "," ~ double ~ "," ~ (CharsWhile(_ != ')').! ~ ")"))
      .map((Coordinates.apply _).tupled)
  }
  private val message = P((greeting | coordinates) ~ End)

  def parse(s: String): Option[Message] = message.unapply(s)
}
