package co.upvest.mberndt

sealed trait Message
case class Coordinates(lat: Double, long: Double, description: String) extends Message
case class Greeting(number: Int) extends Message

object Message extends MessageImpl