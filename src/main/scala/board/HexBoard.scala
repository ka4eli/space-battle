package board

import grid.Grid
import grid.Grid.Shot
import grid.Grid.ShotResult._

import scala.collection.mutable.ArrayBuffer

class HexBoard(grid: Grid) extends Board[String] {
  type ShotType = String

  private var hit = Set.empty[Shot]
  private var missed = Set.empty[Shot]

  private def alive: Set[(Int, Int)] = grid.ships.flatten

  def processShoot(salvo: List[ShotType]) = {
    val results = grid.shoot(salvo.map(hexToShot))
    results.foreach {
      case res@((_, Kill) | (_, Hit)) => hit += res._1
      case (shot, Miss) => missed += shot
    }
    results.map(p => (shotToHex(p._1), p._2))
  }

  def board = fillBoard(withAlive = true, withMissed = true, withHit = true)

  def playerBoard = fillBoard(withAlive = true, withMissed = false, withHit = false)

  def enemyBoard = fillBoard(withAlive = false, withMissed = true, withHit = true)

  private def fillBoard(withAlive: Boolean, withMissed: Boolean, withHit: Boolean) = {
    val buf = ArrayBuffer.fill(grid.size._1)(ArrayBuffer.fill(grid.size._2)("."))
    if (withAlive) alive.foreach(x => buf(x._1)(x._2) = "*")
    if (withHit) hit.foreach(x => buf(x._1)(x._2) = "X")
    if (withMissed) missed.foreach(x => buf(x._1)(x._2) = "-")
    buf.map(_.toVector).toVector
  }

  private def shotToHex(shot: (Int, Int)): String = {
    def intToHex(i: Int): String = i match {
      case 10 => "A"
      case 11 => "B"
      case 12 => "C"
      case 13 => "D"
      case 14 => "E"
      case 15 => "F"
      case _ => i.toString
    }
    intToHex(shot._1) + "x" + intToHex(shot._2)
  }

  private def hexToShot(hex: String): (Int, Int) = {
    def hexToInt(hex: String): Int = hex.toUpperCase match {
      case "A" => 10
      case "B" => 11
      case "C" => 12
      case "D" => 13
      case "E" => 14
      case "F" => 15
      case x if x.toInt > 15 || x.toInt < 0 => throw new HexBoardCoordinateException("Wrong hex coordinates: " + x)
      case x => x.toInt
    }
    val a = hex.split("x").map(hexToInt)
    (a(0), a(1))
  }

}
