package co.upvest.mberndt
import com.cibo.leaflet.{LatLng, Leaflet, LeafletMap}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, MultiMountPoint, Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.{Element, Event, EventSource, MessageEvent}
import org.scalajs.dom.raw.{HTMLDivElement, Node}
import org.scalajs.{dom => d}

import scala.collection.GenSeq

object Main {

  val evenGreetings: Vars[Greeting] = Vars.empty
  val oddGreetings: Vars[Greeting] = Vars.empty
  val coordinates: Vars[Coordinates] = Vars.empty

  def view: Binding[BindingSeq[Node]] = Binding {
    Constants(
      greets(evenGreetings).bind,
      greets(oddGreetings).bind,
      map.bind
    )
  }

  @dom
  def greets(greets: BindingSeq[Greeting]): Binding[HTMLDivElement] =
    <div>{
      for (g <- greets) yield {
        <div>{g.toString}</div>
      }
    }</div>

  def subscribeGreetings(which: String, greets: Vars[Greeting]): EventSource = {
    val source = new d.EventSource(s"/greets/$which")
    source.addEventListener("greeting", { (msg: MessageEvent) =>
      val greet = Greeting(msg.data.toString.toInt)
      greets.value += greet
      if (greets.value.lengthCompare(10) > 0)
        greets.value.remove(0)
    })
    source
  }

  class MapMountPoint(parent: Element, coordinates: BindingSeq[Coordinates])
    extends MultiMountPoint(coordinates) {
    var map: LeafletMap = _
    override protected def mount(): Unit = {
      super.mount()
      map = Leaflet.mapFromNode(parent).setView(LatLng(51.505, -0.09), 13)
      Leaflet.tileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map)
      d.console.log("initialized")
    }

    override protected def unmount(): Unit = {
      map.remove()
      d.console.log("removed")
      super.unmount()
    }

    override protected def set(children: Seq[Coordinates]): Unit = ()

    override protected def splice(from: Int, that: GenSeq[Coordinates], replaced: Int): Unit = ()
  }

  @dom
  def map: Binding[HTMLDivElement] = {
    val e = <div id="myMap"></div>
    new MapMountPoint(e, coordinates).bind
    e
  }

  def main(args: Array[String]): Unit = {
    d.window.addEventListener("load", { (_: Event) =>
      dom.render(d.document.body, view)
      subscribeGreetings("even", evenGreetings)
      subscribeGreetings("odd", oddGreetings)

    })
  }

}
