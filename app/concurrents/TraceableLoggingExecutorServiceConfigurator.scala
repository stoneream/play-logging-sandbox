package concurrents

import com.typesafe.config.Config
import org.apache.pekko.dispatch.{DispatcherPrerequisites, ExecutorServiceConfigurator, ExecutorServiceFactory}

import java.util.concurrent.{ExecutorService, ThreadFactory, TimeUnit}

class TraceableLoggingExecutorServiceConfigurator(
    config: Config,
    prerequisites: DispatcherPrerequisites
) extends ExecutorServiceConfigurator(config, prerequisites) {
  override def createExecutorServiceFactory(id: String, threadFactory: ThreadFactory): ExecutorServiceFactory = {
    new ExecutorServiceFactory {
      override def createExecutorService: ExecutorService = {
        new TraceableLoggingThreadPoolExecutor(
          config.getInt("core-pool-size"),
          config.getInt("max-pool-size"),
          config.getLong("keep-alive-time"),
          TimeUnit.MILLISECONDS
        )
      }
    }
  }
}
