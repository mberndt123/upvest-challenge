package co.upvest.mberndt

import fastparse.noApi._

import ParseUtils.{double, int}
import fastparse.WhitespaceApi

sealed trait Message
case class Coordinates(lat: Double, long: Double, description: String) extends Message
case class Greeting(number: Int) extends Message

object Parse extends WhitespaceApi.Wrapper({
  import fastparse.all._
  NoTrace(CharsWhileIn(" \t", 0))
})

object Message {
import Parse._
  private val greeting = P("Greets" ~ "(" ~/ "Privet" ~ ":" ~ "{" ~ int.map(Greeting.apply) ~ "}" ~ ")")
  private val coordinates = P {
    ("Coordinates" ~ "(" ~/ double ~ "," ~ double ~ "," ~ CharsWhile.raw(_ != ')').! ~ ")")
      .map((Coordinates.apply _).tupled)
  }
  val message = P((greeting | coordinates) ~ End)

  def parse(s: String): Option[Message] = message.unapply(s)
}
