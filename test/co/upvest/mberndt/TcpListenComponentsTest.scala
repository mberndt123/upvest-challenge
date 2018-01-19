package co.upvest.mberndt

import java.nio.charset.StandardCharsets

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source, Tcp}
import akka.util.ByteString
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.components.OneAppPerTestWithComponents
import play.api.routing.Router
import play.api.{BuiltInComponents, BuiltInComponentsFromContext, NoHttpFiltersComponents}

import scala.collection.mutable.ListBuffer

class TcpListenComponentsTest
  extends FunSuite
    with OneAppPerTestWithComponents
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with Eventually {
  val messageBuffer: ListBuffer[Message] = ListBuffer.empty[Message]
  override def components: BuiltInComponents = {
    messageBuffer.clear()
    new BuiltInComponentsFromContext(context)
      with TcpListenComponents
      with NoHttpFiltersComponents {
      override def router: Router = Router.empty
      override def sink: Sink[Message, _] = Sink.foreach(messageBuffer += _)
      override def source: Source[Message, _] = Source.empty
    }
  }

  test("should receive and parse events and dump them to the sink") {
    implicit val m: Materializer = app.materializer
    val future = Source.single {
      val str =
        "Greets(Privet: {815548195})\r\n" +
        "Coordinates(-149.4331873,60.12892832,Starbucks - AK - Seward  00025)\r\n"
      ByteString(str, StandardCharsets.UTF_8)
    }.via(Tcp(app.actorSystem).outgoingConnection("localhost", 9011))
     .to(Sink.ignore)
     .run

    eventually {
      messageBuffer should contain inOrderOnly(
        Greeting(815548195),
        Coordinates(-149.4331873, 60.12892832, "Starbucks - AK - Seward  00025")
      )
    }
  }
}
