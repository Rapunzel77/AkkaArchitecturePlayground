package nara.util

import akka.actor.{ActorLogging, ReceiveTimeout}
import akka.contrib.pattern.ReceivePipeline
import akka.contrib.pattern.ReceivePipeline.HandledCompletely

import scala.concurrent.duration._


trait InactiveTimeout extends ReceivePipeline { this: ActorLogging =>
  def inactiveTimeout: FiniteDuration = 10.seconds
  context.setReceiveTimeout(inactiveTimeout)

  pipelineInner {
    case ReceiveTimeout =>
      log.error("timeout due to inactivity in {}", getClass.getName)
      context.stop(self)
      HandledCompletely
  }
}
