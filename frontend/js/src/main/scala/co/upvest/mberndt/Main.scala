package co.upvest.mberndt
import com.thoughtworks.binding.Binding.{BindingSeq, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{HTMLDivElement, HTMLElement, Node}
import org.scalajs.dom.{Event, EventSource, MessageEvent}
import org.scalajs.{dom => d}

object Main {

  val evenGreetings: Vars[Greeting] = Vars.empty
  val oddGreetings: Vars[Greeting] = Vars.empty
  val coordinates: Vars[Coordinates] = Vars.empty

  @dom
  def view: Binding[Node] =
    <div class="w3-row w3-padding">
      <div class="w3-quarter w3-padding">
        <h1>Even</h1>
        {greets(evenGreetings).bind}
      </div>
      {map.bind}
      <div class="w3-quarter w3-padding">
        <h1>Odd</h1>
        {greets(oddGreetings).bind}
      </div>
    </div>

  @dom
  def greets(greets: BindingSeq[Greeting]): Binding[HTMLElement] =
    <ul class="w3-ul">{
      for (g <- greets) yield {
        <li>{g.toString}</li>
      }
    }</ul>

  @dom
  def map: Binding[HTMLDivElement] = {
    val e = <div id="myMap" class="w3-half w3-padding"></div>
    new MapMountPoint(e, coordinates).bind
    e
  }

  def subscribe[A](url: String, msg: String, vars: Vars[A])(parse: String => A): EventSource = {
    val source = new d.EventSource(url)
    source.addEventListener(msg, { (msg: MessageEvent) =>
      val a = parse(msg.data.toString)
      vars.value += a
      if (vars.value.lengthCompare(10) > 0) {
        vars.value.remove(0)
      }
    })
    source
  }

  def subscribeGreetings(which: String, greets: Vars[Greeting]): EventSource = {
    subscribe(s"/greets/$which", "greeting", greets) { s =>
      Greeting(s.toInt)
    }
  }

  def subscribeCoordinates(value: Vars[Coordinates]): EventSource = {
    subscribe("/coordinates", "coordinates", value) { s =>
      val json = scala.scalajs.js.JSON.parse(s)
      Coordinates(
        json.lat.asInstanceOf[Double],
        json.long.asInstanceOf[Double],
        json.description.asInstanceOf[String]
      )
    }
  }

  def main(args: Array[String]): Unit = {
    d.window.addEventListener("load", { (_: Event) =>
      dom.render(d.document.body, view)
      subscribeGreetings("even", evenGreetings)
      subscribeGreetings("odd", oddGreetings)
      subscribeCoordinates(coordinates)
    })
  }

}
