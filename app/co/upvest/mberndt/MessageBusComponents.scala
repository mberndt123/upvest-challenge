package co.upvest.mberndt

import akka.NotUsed
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import akka.stream.{KillSwitches, UniqueKillSwitch}
import play.api.BuiltInComponents
import play.api.mvc._

import scala.concurrent.Future

trait MessageBusComponents {
  def sink: Sink[Message, _]
  def source: Source[Message, _]
}

trait DefaultMessageBusComponents extends BuiltInComponents {
  def controllerComponents: ControllerComponents

  private val bus: ((Sink[Message, NotUsed], UniqueKillSwitch), Source[Message, NotUsed]) =
    MergeHub.source[Message](perProducerBufferSize = 16)
      .viaMat(KillSwitches.single)(Keep.both)
      .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both).run

  val ((sink, _), source) = bus
  source.to(Sink.ignore).run

  // This is needed because no data will be processed unless
  // there's at least one consumer
  private val killSwitch = bus._1._2

  applicationLifecycle.addStopHook { () =>
    Future(killSwitch.shutdown())
  }

}
