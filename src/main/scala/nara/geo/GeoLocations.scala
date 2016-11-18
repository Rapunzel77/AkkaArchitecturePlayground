package nara.geo

import akka.actor.{Actor, ActorLogging, Props}
import nara.geo.OpenSteetMaps.ResultingCoordinates


object GeoLocations {
  final val Name = "geolocations"
  def apply() = Props (new GeoLocations())

  case class AddressToLocation (address: Address) // replies Coordinates
}

class GeoLocations extends Actor with ActorLogging {

  import GeoLocations._


  override def receive = {
    case msg: AddressToLocation => onAddressToLocation(msg)
    case msg: ResultingCoordinates => onResultingCoordinates(msg)
  }

  def onAddressToLocation(msg: AddressToLocation): Unit = {
    sender() ! Coordinates(52.123, 10.37)
  }

  def onResultingCoordinates(msg: ResultingCoordinates): Unit = {
    sender()
  }

}