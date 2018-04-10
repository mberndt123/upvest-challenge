package co.upvest.mberndt
import com.thoughtworks.binding.Binding.{BindingSeq, Vars}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{HTMLDivElement, HTMLElement, Node}
import org.scalajs.dom.{Event, EventSource, MessageEvent}
import org.scalajs.{dom => d}
import scalatags.JsDom.all._
import scalaz.syntax.functor._

object Main {
  @dom
  def view(
    evenGreetings: Binding[HTMLElement],
    oddGreetings: Binding[HTMLElement],
    coordinates: Binding[HTMLElement]
  ): Binding[Node] =
    <div class="w3-row w3-padding">
      <div class="w3-quarter w3-padding">
        <h1>Even</h1>
        {evenGreetings.bind}
      </div>
      {coordinates.bind}
      <div class="w3-quarter w3-padding">
        <h1>Odd</h1>
        {oddGreetings.bind}
      </div>
    </div>

  @dom
  def greets(greets: BindingSeq[Greeting]): Binding[HTMLElement] =
    <ul class="w3-ul">{
      for (g <- greets) yield {
        <li>{g.toString}</li>
      }
    }</ul>

  def map(coordinates: BindingSeq[Coordinates]): Binding[HTMLDivElement] = {
    val e = div(`class` := "w3-half w3-padding", id := "myMap").render
    (new MapMountPoint(e, coordinates): Binding[Unit]).map(_ => e)
  }

  def subscribe[A](url: String, msg: String)(parse: String => A): (EventSource, BindingSeq[A]) = {
    val vars = Vars.empty[A]
    val source = new d.EventSource(url)
    source.addEventListener(msg, { (msg: MessageEvent) =>
      val a = parse(msg.data.toString)
      vars.value += a
      if (vars.value.lengthCompare(10) > 0) {
        vars.value.remove(0)
      }
    })
    (source, vars)
  }

  def subscribeGreetings(which: String): (EventSource, BindingSeq[Greeting]) = {
    subscribe(s"/greets/$which", "greeting") { s =>
      Greeting(s.toInt)
    }
  }

  def subscribeCoordinates(): (EventSource, BindingSeq[Coordinates]) = {
    subscribe("/coordinates", "coordinates") { s =>
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
      val (_, evenGreetings) = subscribeGreetings("even")
      val (_, oddGreetings) = subscribeGreetings("odd")
      val (_, coordinates) = subscribeCoordinates()
      dom.render(d.document.body,
        view(
          greets(evenGreetings),
          greets(oddGreetings),
          map(coordinates))
        )
    })
  }

}
