package board

import grid.Grid.ShotResult._
import grid.Grid._

import scala.collection.mutable.ArrayBuffer

class HexEnemyBoard extends EnemyBoard[String, String] with HexConversions {
  val size = (16, 16)

  private var killed = 0
  private var hit = Set.empty[Shot]
  private var missed = Set.empty[Shot]

  def shipsKilled = killed

  def board = {
    val buf = ArrayBuffer.fill(size._1)(ArrayBuffer.fill(size._2)("."))
    hit.foreach(x => buf(x._1)(x._2) = "X")
    missed.foreach(x => buf(x._1)(x._2) = "-")
    buf.map(_.toVector).toVector
  }

  def processShotResults(results: List[(String, ShotResult)]) = {
    results.foreach {
      case (hex, Hit) => hit += hexToShot(hex)
      case (hex, Kill) =>
        hit += hexToShot(hex)
        killed += 1
      case (hex, Miss) =>
        missed += hexToShot(hex)
    }
  }
}
