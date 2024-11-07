package filters

import org.apache.pekko.stream.Materializer
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessLog @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  private val logger = Logger(this.getClass)

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    logger.info(s"Request: ${rh.method} ${rh.uri}")
    f(rh).map { result =>
      logger.info(s"Response: ${rh.method} ${rh.uri} (${result.header.status})")
      result
    }
  }

}
