package com.xebialabs.config

object Configuration {


  import com.typesafe.config.ConfigFactory

  private val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)

  lazy val host = config.getString("xl-spaceship.host")
  lazy val port = config.getInt("xl-spaceship.port")

}
