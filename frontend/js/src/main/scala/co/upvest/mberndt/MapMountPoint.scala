package co.upvest.mberndt
import com.cibo.leaflet._
import com.thoughtworks.binding.Binding.{BindingSeq, MultiMountPoint}
import org.scalajs.dom.html.Element
import org.scalajs.dom

import scala.collection.mutable
import scala.collection.GenSeq

class MapMountPoint(parent: Element, coordinates: BindingSeq[Coordinates])
  extends MultiMountPoint[Coordinates](coordinates) {
  var map: LeafletMap = _
  var points = mutable.ArrayBuffer.empty[Layer]

  override protected def mount(): Unit = {
    super.mount()
    dom.window.setTimeout({ () =>
      // this must be run _after_ the node is inserted into the DOM tree, hence the setTimeout.
      map = Leaflet.mapFromNode(parent).setView(LatLng(0.0, 0.0), 1)
      Leaflet.tileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map)
    }, 0)
  }

  override protected def unmount(): Unit = {
    map.remove()
    dom.console.log("removed")
    super.unmount()
  }

  override protected def set(children: Seq[Coordinates]): Unit = {
    splice(0, children, points.size)
  }

  override protected def splice(from: Int, that: GenSeq[Coordinates], replaced: Int): Unit = {
    points.slice(from, from + replaced).foreach(map.removeLayer)
    points.remove(from, replaced)
    points.insertAll(from, that.map { c =>
      val marker = L.marker(LatLng(c.lat, c.long))
      marker.asInstanceOf[scala.scalajs.js.Dynamic].bindTooltip(c.description).openTooltip()
      marker.addTo(map)
    }.seq)
  }
}
