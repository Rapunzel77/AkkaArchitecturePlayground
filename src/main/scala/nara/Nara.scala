package nara

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import nara.geo.GeoLocations


object Nara {
  final val Name = "nara"
  def apply() = Props (new Nara())
}

class Nara extends Actor with ActorLogging {
  val geoLocations = context.actorOf(GeoLocations(), GeoLocations.Name)
  context.watch (geoLocations)

  override def receive = {
    case Terminated(_) => System.exit(1)
  }
}
