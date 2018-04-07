package co.upvest.mberndt
import com.cibo.leaflet.{LatLng, Leaflet, LeafletMap}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, MultiMountPoint, Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.{Element, Event, EventSource, MessageEvent}
import org.scalajs.dom.raw.{HTMLDivElement, HTMLElement, Node}
import org.scalajs.{dom => d}

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
  def greets(greets: BindingSeq[Greeting]): Binding[HTMLElement] =
    <ul>{
      for (g <- greets) yield {
        <li>{g.toString}</li>
      }
    }</ul>

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

  def subscribeCoordinates(value: Binding.Vars[Coordinates]): EventSource = {
    val source = new d.EventSource(s"/coordinates")
    source.addEventListener("coordinates", { (msg: MessageEvent) =>
      val json = scala.scalajs.js.JSON.parse(msg.data.toString)
      val coordinate = Coordinates(
        json.lat.asInstanceOf[Double],
        json.long.asInstanceOf[Double],
        json.description.asInstanceOf[String]
      )
      value.value += coordinate
      if (value.value.lengthCompare(10) > 0) {
        value.value.remove(0)
      }
    })
    source
  }

  @dom
  def map: Binding[HTMLDivElement] = {
    val e = <div id="myMap"></div>
    new MapMountPoint(e, coordinates).bind
    e
  }

  def main(args: Array[String]): Unit = {
    d.window.addEventListener("load", { (_: Event) =>
      val div = d.document.createElement("div")
      div.setAttribute("id", "myMap") // my stylesheet contains the formatting for this ID
      val map = Leaflet.mapFromNode(div).setView(LatLng(51.505, -0.09), 13)
      Leaflet.tileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map)
      d.document.body.appendChild(div)


      dom.render(d.document.body, view)
      subscribeGreetings("even", evenGreetings)
      subscribeGreetings("odd", oddGreetings)
      subscribeCoordinates(coordinates)
    })
  }

}
