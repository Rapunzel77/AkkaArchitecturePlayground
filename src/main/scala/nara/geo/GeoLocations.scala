package nara.geo

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.HttpExt

object GeoLocations {
  final val Name = "geolocations"
  def apply(http: HttpExt) = Props(new GeoLocations(http))

  case class AddressToLocation(address: Address) // replies Coordinates
}

class GeoLocations(http: HttpExt) extends Actor with ActorLogging {
  import GeoLocations._

  override def receive = {
    case msg: AddressToLocation => onAddressToLocation(msg)
  }

  def onAddressToLocation(msg: AddressToLocation) = context.actorOf (OpenStreetMaps (http, sender), OpenStreetMaps.Name) ! msg

}
