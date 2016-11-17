package nara

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.util.Timeout
import nara.geo.{Address, GeoLocations}
import nara.geo.GeoLocations.AddressToLocation


object Nara {
  final val Name = "nara"
  def apply() = Props (new Nara())
}

class Nara extends Actor with ActorLogging {
  import scala.concurrent.duration._
  import akka.pattern.ask

  import context.dispatcher
  implicit val timeout:Timeout = 1.minute

  val geoLocations = context.actorOf(GeoLocations(), GeoLocations.Name)
  context.watch (geoLocations)

  val api = context.actorOf (Api(geoLocations, "localhost", 8080), Api.Name)
  context.watch (api)

  val f = geoLocations ? AddressToLocation(Address("Bob'n de Lieth 55", "Bad Bramstedt", "Germany"))
  f.onComplete(println)

  override def receive = {
    case Terminated(_) => System.exit(1)
  }
}
