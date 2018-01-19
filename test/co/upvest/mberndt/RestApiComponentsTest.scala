package co.upvest.mberndt

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.{Assertion, FunSuite, Matchers}
import org.scalatestplus.play.components.OneAppPerTestWithComponents
import play.api.test.FakeRequest
import play.api.{BuiltInComponents, BuiltInComponentsFromContext}
import play.filters.HttpFiltersComponents
import play.api.test.{Helpers => H}

import scala.collection.immutable.Iterable

class RestApiComponentsTest
  extends FunSuite
  with OneAppPerTestWithComponents
  with Matchers {
  override def components: BuiltInComponents =
    new BuiltInComponentsFromContext(context)
      with HttpFiltersComponents
      with RestApiComponents {
      override def sink: Sink[Message, _] = Sink.ignore
      override def source: Source[Message, _] = Source(Iterable(
        Greeting(5),
        Greeting(42),
        Coordinates(52.521918, 13.413215, "Alex")
      ))
    }

  def testRequest(path: String)(expectedBody: String): Assertion = {
    import H._ // needed to get the implicits
    implicit val m: Materializer = app.materializer // H.contentAsString needs a Materializer
    val result = H.route(app, FakeRequest(H.GET, path)).get
    H.status(result) shouldBe H.OK
    H.contentAsString(result) shouldBe expectedBody

  }

  def testGreeting(n: Int, evenOrOdd: String): Assertion = {
    testRequest(s"/greets/$evenOrOdd") {
      s"""event: greeting
         |data: $n
         |
         |""".stripMargin
    }
  }

  test("even greetings") {
    testGreeting(42, "even")
  }

  test("odd greetings") {
    testGreeting(5, "odd")
  }

  test("coordinates") {
    testRequest("/coordinates") {
      s"""event: coordinates
         |data: {"lat":52.521918,"long":13.413215,"description":"Alex"}
         |
         |""".stripMargin
    }
  }
}
