package board

import grid.Grid.ShotResult._

trait Board[T] {
  type ShotType

  def board: Vector[Vector[T]]

  def playerBoard: Vector[Vector[T]]

  def enemyBoard: Vector[Vector[T]]

  def processShoot(salvo: List[ShotType]): List[(ShotType, ShotResult)]

  def board2String = board.map(_.mkString).mkString("\n")
}

class HexBoardCoordinateException(mes: String) extends Exception(mes)
