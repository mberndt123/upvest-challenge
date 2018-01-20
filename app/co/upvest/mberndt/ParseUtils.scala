package co.upvest.mberndt

import fastparse.all._

import scala.util.control.NonFatal

object ParseUtils {
  // toInt can throw a NumberFormatException if there are too many digits,
  // so we need to guard against that
  private def tryMap[A, B](p: Parser[A])(f: A => B): Parser[B] = p.flatMap { a =>
    try PassWith(f(a))
    catch { case NonFatal(_) => Fail }
  }
  private val digits = P(CharsWhile(_.isDigit))
  val double: Parser[Double] =
    P(tryMap(("-".? ~ digits ~ ("." ~ digits).?).!)(_.toDouble))
  val int: Parser[Int] =
    P(tryMap(("-".? ~ digits).!)(_.toInt))
}
