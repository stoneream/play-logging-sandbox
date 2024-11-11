package concurrents

import org.slf4j.MDC

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor}

class TraceableLoggingThreadPoolExecutor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: java.util.concurrent.TimeUnit
) extends ThreadPoolExecutor(
      corePoolSize,
      maximumPoolSize,
      keepAliveTime,
      unit,
      new LinkedBlockingQueue[Runnable]
    ) {
  
  override def execute(command: Runnable): Unit = {
    super.execute(wrap(command))
  }

  private def wrap(runnable: Runnable): Runnable = {
    val contextMap = MDC.getCopyOfContextMap
    () => {
      val beforeContext = MDC.getCopyOfContextMap
      if (contextMap == null) {
        MDC.clear()
      } else {
        MDC.setContextMap(contextMap)
      }
      try {
        runnable.run()
      } finally {
        if (beforeContext == null) {
          MDC.clear()
        } else
          MDC.setContextMap(beforeContext)
      }
    }
  }
}
