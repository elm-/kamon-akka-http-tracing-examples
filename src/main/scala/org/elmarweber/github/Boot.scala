package org.elmarweber.github

import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon

object Boot extends App with ServiceRoute with StrictLogging {
  Kamon.start()

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  Kamon.tracer.subscribe(system.actorOf(Props(new TraceCollectorActor())))

  
  Http().bindAndHandle(serviceRoute, Config.service.http.interface, Config.service.http.port).transform(
    binding => logger.info(s"REST interface bound to ${binding.localAddress} "), { t => logger.error(s"Couldn't bind interface: ${t.getMessage}", t); sys.exit(1) }
  )
}
