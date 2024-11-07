lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-logging-sandbox""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.15", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "net.logstash.logback" % "logstash-logback-encoder" % "8.0",
      // jackson について
      // https://github.com/auth0/jwks-rsa-java/issues/192#issuecomment-2171800538
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ),
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
