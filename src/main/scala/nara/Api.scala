package nara

import akka.actor.{Props, Actor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.pattern.pipe

object Api {
  import Directives._

  final val Name = "http"
  def apply(address: String, port: Int) = Props (new Api (address, port))

  def route () = {
    path ("hallo") {
      get {
        complete ("yo")
      }
    }
  }
}


class Api(address: String, port: Int) extends Actor{
  import Api._

  implicit val mat = ActorMaterializer()

  Http (context.system)
    .bindAndHandle(route(), address, port)
    .pipeTo(self)
}
