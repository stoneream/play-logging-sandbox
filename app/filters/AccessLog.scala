package filters

import org.apache.pekko.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessLog @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {

    logger.info("Request: {} {}", kv("method", rh.method), kv("uri", rh.uri))
    f(rh).map { result =>
      logger.info("Response: {} {} ({})", kv("method", rh.method), kv("uri", rh.uri), kv("status", result.header.status))
      result
    }
  }

}
