package com.xebialabs.board

import com.xebialabs.grid.Grid.ShotResult._
import com.xebialabs.grid.HexGrid

import scala.collection.mutable.ArrayBuffer

class HexPlayerBoard(grid: HexGrid) extends PlayerBoard[String, String] with HexConversions {
  def size = grid.size

  private def alive: Set[(Int, Int)] = grid.ships.flatten

  def shipsAlive = grid.ships.size

  def shipsKilled = grid.killed

  def processSalvo(salvo: List[String]): List[(String, ShotResult)] =
    grid.shoot(salvo.map(hexToShot)).map(p => (shotToHex(p._1), p._2))

  def board = {
    val buf = ArrayBuffer.fill(size._1)(ArrayBuffer.fill(size._2)("."))
    alive.foreach(x => buf(x._1)(x._2) = "*")
    buf.map(_.toVector).toVector
  }

  override def board2String = (Vector((0 to size._1 - 1).map(intToHex).toVector) ++ board).zipWithIndex.map {
    case x if x._2 != 0 => intToHex(x._2 - 1) +: x._1
    case x => "#" +: x._1
  }.map(_.mkString(" ")).mkString("\n")

}
