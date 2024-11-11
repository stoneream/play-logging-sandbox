package filters

import org.apache.pekko.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.{LoggerFactory, MDC}

import java.security.SecureRandom
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class AccessLogFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  private val random = new Random(new SecureRandom())

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    val traceId = random.alphanumeric.take(8).mkString
    MDC.put("traceId", traceId)
    
    logger.info(
      "Request: {} {}",
      kv("method", rh.method),
      kv("uri", rh.uri)
    )
    f(rh).map { result =>
      logger.info(
        "Response: {} {} ({})",
        kv("method", rh.method),
        kv("uri", rh.uri),
        kv("status", result.header.status)
      )
      result
    }
  }

}
