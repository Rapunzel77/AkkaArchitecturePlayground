package nara.geo

import akka.pattern.pipe
import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, HttpRequest, Uri}
import akka.http.scaladsl.model.Uri.{apply, Query}
import akka.stream.ActorMaterializer
import nara.geo.GeoLocations.AddressToLocation

object OpenSteetMaps{
  final val Name ="OpenStreetMaps"
  def apply() = Props(new OpenStreetMaps())

  case class ResultingCoordinates(coordinates:Coordinates)
}

class OpenStreetMaps extends Actor with ActorLogging{
  import context.dispatcher
  implicit val mat = ActorMaterializer()
  val http = Http(context.system)



  override def receive = {
    case msg: AddressToLocation => onMessageToLocation(msg)
    case (replyTo:ActorRef, response:HttpResponse) => onResponse(replyTo, response)
  }

  def onMessageToLocation(msg: AddressToLocation): Unit = {
    val theSender = sender()
    val uri = Uri ("http://nominatim.openstreetmap.org/search")
                .withQuery(Query(
                                 "format"  -> "json",
                                 "street"  -> msg.address.street,
                                 "city"    -> msg.address.city,
                                 "country" -> msg.address.country))
    http.singleRequest(HttpRequest(uri=uri))
        .map((theSender,_))
        .pipeTo(self)
  }

  def onResponse(replyTo: ActorRef, response: HttpResponse): Unit = {
    response.entity.
  }
}
