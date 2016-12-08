package nara.geo

import akka.actor.{Actor, ActorLogging, Props}

object GeoLocations {
  final val Name = "geolocations"
  def apply() = Props(new GeoLocations())

  case class AddressToLocation(address: Address) // replies Coordinates
}

class GeoLocations extends Actor with ActorLogging {
  import GeoLocations._

  val osm = context.actorOf(OpenStreetMaps(), OpenStreetMaps.Name)

  override def receive = {
    case msg: AddressToLocation => onAddressToLocation(msg)
  }

  def onAddressToLocation(msg: AddressToLocation) = osm.tell(msg, sender)

}
