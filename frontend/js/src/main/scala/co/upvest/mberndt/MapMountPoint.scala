package co.upvest.mberndt
import com.cibo.leaflet._
import com.thoughtworks.binding.Binding.BindingSeq
import org.scalajs.dom.html.Element
import org.scalajs.dom

import scala.collection.mutable
import scala.collection.GenSeq
import scala.scalajs.js

class MapMountPoint(parent: Element, coordinates: BindingSeq[Coordinates])
  extends StatefulMultiMountPoint[Coordinates](coordinates) {
  protected case class State(map: LeafletMap, points: mutable.ArrayBuffer[Layer])
  override protected def init(): State = {
    val map = Leaflet.mapFromNode(parent).setView(LatLng(0.0, 0.0), 1)
    Leaflet.tileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map)
    dom.window.setTimeout({ () =>
      // this must be run _after_ the node is inserted into the DOM tree, hence the setTimeout.
      map.asInstanceOf[js.Dynamic].invalidateSize()
    }, 0)
    State(map, mutable.ArrayBuffer.empty[Layer])
  }

  override protected def destroy(s: State): Unit =
    s.map.remove()

  override protected def set(state: State, children: Seq[Coordinates]): Unit = {
    splice(state, 0, children, state.points.size)
  }

  override protected def splice(state: State, from: Int, that: GenSeq[Coordinates], replaced: Int): Unit = {
    import state._
    points.slice(from, from + replaced).foreach(map.removeLayer)
    points.remove(from, replaced)
    points.insertAll(from, that.map { c =>
      val marker = L.marker(LatLng(c.lat, c.long))
      marker.asInstanceOf[scala.scalajs.js.Dynamic]
        .bindTooltip(dom.document.createTextNode(c.description))
        // we need to use createTextNode here, otherwise it will interpret the
        // description as HTML, leading to injection attacks
        .openTooltip()
      marker.addTo(map)
    }.seq)

  }
}
