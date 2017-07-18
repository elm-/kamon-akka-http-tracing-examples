package org.elmarweber.github

import spray.json.DefaultJsonProtocol


case class EchoResponse(echo: String)

object EchoResponse extends DefaultJsonProtocol {
  implicit val EchoResponseFormat = jsonFormat1(EchoResponse.apply)
}