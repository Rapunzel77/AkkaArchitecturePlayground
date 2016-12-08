package nara

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import akka.pattern.{ask, pipe}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import nara.geo.{Address, Coordinates}
import nara.geo.GeoLocations.AddressToLocation

import scala.concurrent.duration._
import scala.util.Failure

object Api {
  import Directives._

  final val Name = "http"
  def apply(geoLocations: ActorRef, address: String, port: Int) =
    Props(new Api(geoLocations, address, port))

  implicit val timeout: Timeout = 10.seconds

  def route(geoLocations: ActorRef) = {
    handleExceptions (ExceptionHandler {
      case exc: IllegalArgumentException => complete (HttpResponse (StatusCodes.BadRequest, entity = HttpEntity(exc.getMessage)))
    }) {
      path ("hallo") {
        get {
          complete ("yo!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        }
      } ~
      path ("loc" / Segment / Segment / Segment) { (street, city, country) =>
        onSuccess (geoLocations ? AddressToLocation (Address (street, city, country))) {
          case Coordinates (lat, long) => complete (s"--> $lat / $long")
        }
      }
    }
  }
}

class Api(geoLocations: ActorRef, address: String, port: Int)
    extends Actor
    with ActorLogging {
  import Api._
  import context.dispatcher

  implicit val mat = ActorMaterializer()

  Http(context.system)
    .bindAndHandle(route(geoLocations), address, port)
    .pipeTo(self)

  override def receive = {
    case ServerBinding(a) =>
      log.info("Bound to {}", a)
      context.become(Actor.emptyBehavior)

    case Failure(c) =>
      log.error(c, "Can't bind to {}:{}", address, port)
      context.stop(self)
  }
}
