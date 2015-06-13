package board

import grid.Grid.ShotResult._
import grid.HexGrid

import scala.collection.mutable.ArrayBuffer

class HexPlayerBoard(grid: HexGrid) extends PlayerBoard[String, String] with HexConversions {
  def size = grid.size

  private def alive: Set[(Int, Int)] = grid.ships.flatten

  def shipsAlive = grid.ships.size

  def processSalvo(salvo: List[String]): List[(String, ShotResult)] =
    grid.shoot(salvo.map(hexToShot)).map(p => (shotToHex(p._1), p._2))

  def board = {
    val buf = ArrayBuffer.fill(size._1)(ArrayBuffer.fill(size._2)("."))
    alive.foreach(x => buf(x._1)(x._2) = "*")
    buf.map(_.toVector).toVector
  }
}
