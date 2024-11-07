package action

import action.TraceableLoggingAction.TraceableRequest
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import play.api.i18n.MessagesApi
import play.api.mvc.{MessagesRequest, _}

import java.security.SecureRandom
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class TraceableLoggingAction @Inject() (
    val parser: BodyParsers.Default,
    val messagesApi: MessagesApi
)(implicit ec: ExecutionContext)
    extends MessagesActionBuilder[TraceableRequest, AnyContent] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val random = new Random(new SecureRandom())

  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](request: Request[A], block: MessagesRequest[A] => Future[Result]): Future[Result] = {
    val traceId = random.alphanumeric.take(8).mkString

    logger.info(
      "Request: {}, {}",
      kv("method", request.method),
      kv("uri", request.uri)
    )

    block(new MessagesRequest(request, messagesApi)).map { result =>
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
  case class TraceableRequest[A](
      traceId: String,
      request: MessagesRequest[A]
  )
}
