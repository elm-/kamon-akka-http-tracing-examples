package org.elmarweber.github

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import kamon.SpanReporter
import kamon.trace.Span

class MySpanReporter extends SpanReporter with StrictLogging {
  override def reportSpans(spans: Seq[Span.CompletedSpan]): Unit = {
     spans.foreach { span =>
       logger.info(span.toString)
     }
  }

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def reconfigure(config: Config): Unit = {}
}
