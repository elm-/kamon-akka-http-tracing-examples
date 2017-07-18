package org.elmarweber.github

import java.io.StringWriter
import java.util.concurrent.atomic.AtomicLong

import akka.http.scaladsl.server.directives.BasicDirectives
import akka.actor._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.BasicDirectives.{extractRequestContext, mapRouteResult}
import kamon.Kamon
import kamon.trace.Tracer

import scala.util.Random

trait RouteLoggingDirective extends BasicDirectives {
  import RouteLoggingDirective._
  
  private val randomSeed = Random.nextInt(100000)

  private val requestIdCounter = new AtomicLong(1)
  protected def additionalTraceId = "trace"

  def trace: Directive0 =
    extractRequestContext.flatMap { ctx ⇒
      val traceId = s"req-$randomSeed-${requestIdCounter.getAndIncrement()}-${additionalTraceId}"
      val spanBuilder = Kamon.tracer.buildSpan(ctx.request.uri.path.toString)
      spanBuilder.withTag("myTraceId", traceId)
      ctx.request.headers.find(_.name == TraceIdHeader).foreach { header =>
        spanBuilder.withTag("traceId", header.value)
      }
      ctx.request.headers.find(_.name == ParentSpanIdHeader).foreach { header =>
        spanBuilder.withTag("parentId", header.value)
      }
      val activeSpan = spanBuilder.startActive()
      mapRouteResult { result ⇒
        activeSpan.close()
        //val responseWithTraceHeader = response.copy(headers = RawHeader(KamonTraceRest.PublicHeaderName, traceId) :: response.headers)
        println(traceId + " done")
        result
      }
    }
}

object RouteLoggingDirective {
  val TraceIdHeader = "X─B3─TraceId"
  val ParentSpanIdHeader = "X─B3─ParentSpanId"
}

