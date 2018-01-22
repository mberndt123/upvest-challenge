package co.upvest.mberndt

import java.nio.charset.StandardCharsets.UTF_8

import akka.stream.scaladsl.{Framing, Sink, Tcp}
import akka.stream.scaladsl.Tcp.IncomingConnection
import akka.util.ByteString
import play.api.{BuiltInComponents, Logger}

import scala.concurrent.Future

trait TcpListenComponents
  extends BuiltInComponents
    with MessageBusComponents {
  private val handleConnection: IncomingConnection => Unit = {
    val flow = Framing.delimiter(ByteString('\r', '\n'), 1000).map { bytes =>
      val string = bytes.decodeString(UTF_8)
      val opt = Message.parse(string)
      if (opt.isEmpty)
        Logger.warn(s"failed to parse message $string")
      opt
    }.collect {
      case Some(msg) =>
        msg
      // This looks a little odd: why use alsoTo and the weird "case _ if false"?
      // I don't want to send anything to the client, so a Sink would be enough,
      // but the API requires a Flow. At first I tried to create a flow using
      // Flow.fromSinkAndSource(mySink, Source.empty), but that leads to every
      // TCP connection being closed immediately after it is established, before
      // we have a chance to read any data.
    }.alsoTo(sink).collect {
      case _ if false => ByteString.empty
    }

    _.handleWith(flow)
  }

  private val connections: Future[Tcp.ServerBinding] =
    Tcp(actorSystem).bind("0.0.0.0", 9011).to(Sink.foreach(handleConnection)).run()

  applicationLifecycle.addStopHook(() =>
    connections.flatMap(_.unbind())
  )

}
