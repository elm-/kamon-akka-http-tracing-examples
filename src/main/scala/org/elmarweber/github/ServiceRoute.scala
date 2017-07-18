package org.elmarweber.github

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import spray.json._

import scala.concurrent.{ExecutionContext, Future}


trait ServiceRoute extends Directives with DefaultJsonProtocol with RouteLoggingDirective {
  val serviceRoute = pathPrefix("api") {
    trace {
      pathPrefix("echo") {
        pathEndOrSingleSlash {
          get {
            parameter("msg".as[String].?) { msg =>
              complete {
                HttpEntity(ContentTypes.`text/plain(UTF-8)`, msg.getOrElse("OK"))
              }
            }
          }
        }
      }
    }
  }
}


trait EchoService {
  def doEcho(msg: Option[String])(implicit ec: ExecutionContext): Unit = {
    Future {
      Thread.sleep(500)
      msg.getOrElse("OK")
    }
  }
}