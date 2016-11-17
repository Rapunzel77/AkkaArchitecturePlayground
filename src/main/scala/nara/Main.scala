package nara

import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration._


object Main extends App {
  val system = ActorSystem()

  system.actorOf (Nara(), Nara.Name)

  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run (): Unit = {
      val f = system.terminate()
      Await.ready(f, 10.seconds)
    }
  })
}
