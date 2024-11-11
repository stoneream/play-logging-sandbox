package action

import action.TraceableLoggingAction.TraceableRequest
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.{LoggerFactory, MDC}
import play.api.i18n.MessagesApi
import play.api.mvc._

import java.security.SecureRandom
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class TraceableLoggingAction @Inject() (
    messagesApi: MessagesApi,
    val parser: BodyParsers.Default
)(implicit ec: ExecutionContext)
    extends ActionBuilder[TraceableRequest, AnyContent] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val random = new Random(new SecureRandom())

  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: TraceableRequest[A] => Future[Result]): Future[Result] = {
    val traceId = random.alphanumeric.take(8).mkString

    MDC.put("traceId", traceId)

    logger.info(
      "Request: {}, {}",
      kv("method", request.method),
      kv("uri", request.uri)
    )

    block(new TraceableRequest(traceId, request, messagesApi)).map { result =>
      logger.info(
        "Response: {}, {} ({})",
        kv("method", request.method),
        kv("uri", request.uri),
        kv("status", result.header.status)
      )
      result
    }
  }
}

object TraceableLoggingAction {

  class TraceableRequest[A](
      val traceId: String,
      request: Request[A],
      messagesApi: MessagesApi
  ) extends MessagesRequest[A](request, messagesApi)

}
