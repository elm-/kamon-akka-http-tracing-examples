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
  private val randomSeed = Random.nextInt(100000)

  private val requestIdCounter = new AtomicLong(1)
  protected def additionalTraceId = "trace"

  def trace: Directive0 =
    extractRequestContext.flatMap { ctx ⇒
      val traceId = s"req-$randomSeed-${requestIdCounter.getAndIncrement()}-${additionalTraceId}"
      println(traceId + " done")

      val traceCtx = Kamon.tracer.newContext(ctx.request.uri.path.toString, Some(traceId))
      Tracer.setCurrentContext(traceCtx)
      ctx.request.headers.find(_.name == "tbd").foreach { header =>
        Tracer.currentContext.addMetadata("parentToken", header.value)
      }
      mapRouteResult { result ⇒
        traceCtx.finish()
        //val responseWithTraceHeader = response.copy(headers = RawHeader(KamonTraceRest.PublicHeaderName, traceId) :: response.headers)
        Tracer.clearCurrentContext
        println(traceId + " done")
        result
      }
    }
}

