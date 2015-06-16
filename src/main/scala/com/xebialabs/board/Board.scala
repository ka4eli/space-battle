package com.xebialabs.board

import com.xebialabs.grid.Grid.ShotResult._

trait Board[ShotType, FieldView] {
  def board: Vector[Vector[FieldView]]
  def size: (Int, Int)
  def board2String = board.map(_.mkString).mkString("\n")
}

trait PlayerBoard[ShotType, FieldView] extends Board[ShotType, FieldView] {
  def shipsAlive: Int
  def shipsKilled: Int
  def processSalvo(salvo: List[ShotType]): List[(ShotType, ShotResult)]
}

trait EnemyBoard[ShotType, FieldView] extends Board[ShotType, FieldView] {
  def shipsKilled: Int
  def processShotResults(results: List[(ShotType, ShotResult)])
}
