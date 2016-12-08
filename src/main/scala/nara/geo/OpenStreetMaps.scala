package nara.geo

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.util.ByteString
import nara.geo.GeoLocations.AddressToLocation
import nara.util.InactiveTimeout
import play.api.libs.json.{JsArray, JsObject, JsString, Json}

private[geo] object OpenStreetMaps {
  final val Name = "OpenStreetMaps"
  def apply(http: HttpExt, replyTo: ActorRef) = Props(new OpenStreetMaps(http, replyTo))
  case class OsmResponse(json: String)
}

private[geo] class OpenStreetMaps(http: HttpExt, replyTo: ActorRef) extends Actor with ActorLogging with InactiveTimeout{
  import OpenStreetMaps._
  import context.dispatcher
  implicit val mat = ActorMaterializer()

  override def receive = {
    case msg: AddressToLocation => onMessageToLocation(msg)
    case HttpResponse(StatusCodes.OK, _, entity, _) => onResponse(entity)
    case OsmResponse (json) => onJsonResponse (json)
    case f: Failure =>
      replyTo ! f
      context.stop(self)
  }

  def onMessageToLocation(msg: AddressToLocation): Unit = {
    val uri = Uri("http://nominatim.openstreetmap.org/search").withQuery(
      Query("format" -> "json",
            "street" -> msg.address.street,
            "city" -> msg.address.city,
            "country" -> msg.address.country))

    http
      .singleRequest(HttpRequest(uri = uri))
      .pipeTo (self)
  }

  def onResponse (entity: HttpEntity) = {
    entity.dataBytes
      .runFold(ByteString(""))(_ ++ _)
      .map(x => OsmResponse (x.decodeString("utf-8")))
      .pipeTo(self)
  }

  private def onJsonResponse (json: String): Unit = {
    Json.parse(json) match {
      case JsArray(els) if els.nonEmpty => els.head match {
        case JsObject(props) =>
          (props.get ("lat"), props.get ("lon")) match {
            case (Some(JsString(lat)), Some(JsString(lon))) => replyTo ! Coordinates (lat.toDouble, lon.toDouble)
          }
      }
      case _ => replyTo ! Failure (new IllegalArgumentException("address not found"))
    }
    context.stop(self)
  }

}
