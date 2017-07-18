package org.elmarweber.github

import akka.actor._
import com.typesafe.scalalogging.StrictLogging
import kamon.trace._

import scala.concurrent.duration._
import scala.util._

class TraceCollectorActor() extends Actor with StrictLogging {
  private var buffer: List[TraceInfo] = Nil
  private implicit def ec = context.system.dispatcher

  override def preStart(): Unit = {
    // TODO: configure sync interval
    context.system.scheduler.scheduleOnce(10.seconds, self, "flush")
  }

  def receive = {
    case "flush" =>
      logger.debug("Flushing")
      if (buffer != Nil) {
        val flushBuffer = buffer
        buffer = Nil
        logger.info(buffer.toString)
      } else {
        context.system.scheduler.scheduleOnce(10.seconds, self, "flush")
      }
    case traceInfo: TraceInfo =>
      buffer = traceInfo :: buffer
  }
}