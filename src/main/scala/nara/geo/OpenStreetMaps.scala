package nara.geo

import akka.actor.Actor.Receive
import akka.actor.Status.Failure
import akka.pattern.pipe
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.Uri.{Query, apply}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import nara.geo.GeoLocations.AddressToLocation
import nara.util.InactiveTimeout
import play.api.libs.json.{JsArray, JsObject, JsString, Json}

private[geo] object OpenStreetMaps {
  final val Name = "OpenStreetMaps"
  def apply() = Props(new OpenStreetMaps())
}

private[geo] class OpenStreetMaps extends Actor with ActorLogging {
  import OpenStreetMaps._
  import context.dispatcher
  implicit val mat = ActorMaterializer()
  val http = Http(context.system)

  override def receive = {
    case msg: AddressToLocation => onMessageToLocation(msg)
  }

  def onMessageToLocation(msg: AddressToLocation): Unit = {
    val uri = Uri("http://nominatim.openstreetmap.org/search").withQuery(
      Query("format" -> "json",
            "street" -> msg.address.street,
            "city" -> msg.address.city,
            "country" -> msg.address.country))

    http
      .singleRequest(HttpRequest(uri = uri))
      .pipeTo (context.actorOf (Conversation (sender)))
  }


  private object Conversation {
    def apply (replyTo: ActorRef) = Props (new Conversation(replyTo))
    case class OsmResponse(json: String)
  }

  private class Conversation(replyTo: ActorRef) extends Actor with ActorLogging with InactiveTimeout {
    import Conversation._

    override def receive = {
      case HttpResponse(StatusCodes.OK, _, entity, _) => onResponse(entity)
      case OsmResponse (json) => onJsonResponse (json)
      case f: Failure =>
        replyTo ! f
        context.stop(self)
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
}
