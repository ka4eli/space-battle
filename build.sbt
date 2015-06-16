name := "XL_Spaceship"

version := "1.0"

scalaVersion := "2.11.6"

val scalatraVersion = "2.3.0"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.scalatra" %% "scalatra" % scalatraVersion,
  "org.scalatra" %% "scalatra-json" % scalatraVersion,
  "javax.servlet" % "javax.servlet-api" % "3.1.0",
  "org.json4s" %% "json4s-jackson" % "3.2.9",
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "org.eclipse.jetty" % "jetty-webapp" % "9.1.5.v20140505" /*% "compile;container"*/,
  "org.eclipse.jetty" % "jetty-plus" % "9.1.5.v20140505" /*% "compile;container"*/ ,
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.ning" % "async-http-client" % "1.9.27"
)
