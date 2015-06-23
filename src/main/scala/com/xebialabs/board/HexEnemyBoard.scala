package com.xebialabs.board

import com.xebialabs.grid.Grid.ShotResult._
import com.xebialabs.grid.Grid._

import scala.collection.mutable.ArrayBuffer

class HexEnemyBoard extends EnemyBoard[String, String] with HexConversions {
  val size = (16, 16)

  private var killed = 0
  private var _hit = Set.empty[Shot]
  private var _missed = Set.empty[Shot]

  def shipsKilled = killed
  def missed = _missed
  def hit = _hit

  def board = {
    val buf = ArrayBuffer.fill(size._1)(ArrayBuffer.fill(size._2)("."))
    _missed.foreach(x => buf(x._1)(x._2) = "-")
    _hit.foreach(x => buf(x._1)(x._2) = "X")
    buf.map(_.toVector).toVector
  }

  def processShotResults(results: List[(String, ShotResult)]) = {
    results.foreach {
      case (hex, Hit) => _hit += hexToShot(hex)
      case (hex, Kill) =>
        _hit += hexToShot(hex)
        killed += 1
      case (hex, Miss) =>
        _missed += hexToShot(hex)
    }
  }

  override def board2String = (Vector((0 to size._1 - 1).map(intToHex).toVector) ++ board).zipWithIndex.map {
    case x if x._2 != 0 => intToHex(x._2 - 1) +: x._1
    case x => "#" +: x._1
  }.map(_.mkString(" ")).mkString("\n")

}
