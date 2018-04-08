package co.upvest.mberndt

import co.upvest.mberndt.ParseUtils.{int, double}
import fastparse.WhitespaceApi
import fastparse.noApi._

object Parse extends WhitespaceApi.Wrapper({
  import fastparse.all._
  NoTrace(CharsWhileIn(" \t", 0))
})

// I'd normally put this directly in the Message companion object. However
// that is compiled for the frontend as well, and we don't want the
// fastparse dependency there, so we have this MessageImpl for the
// backend and an empty MessageImpl for the frontend.
trait MessageImpl {
  import Parse._
  private val greeting = P("Greets" ~ "(" ~/ "Privet" ~ ":" ~ "{" ~ int.map(Greeting.apply) ~ "}" ~ ")")
  private val coordinates = P {
    ("Coordinates" ~ "(" ~/ double ~ "," ~ double ~ "," ~ CharsWhile.raw(_ != ')').! ~ ")")
      .map((Coordinates.apply _).tupled)
  }
  private val message = P((greeting | coordinates) ~ End)

  def parse(s: String): Option[Message] = message.unapply(s)

}
