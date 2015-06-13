package models


case class NewGameRequest(userId: String, fullName: String, spaceshipProtocol: SpaceshipProtocol, rules: Option[String])

case class SpaceshipProtocol(hostname: String, port: Int)

case class NewGameResponse(userId: String, fullName: String, gameId: String, starting: String, rules: Option[String])

case class User(userId: String, fullName: String)