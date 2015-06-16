package com.xebialabs.board

import org.scalatest._
import com.xebialabs.grid.Grid.ShotResult._

import scala.collection.mutable.ArrayBuffer

class HexEnemyBoardTest extends FlatSpec with Matchers {

  def board = ArrayBuffer.fill(16)(ArrayBuffer.fill(16)("."))

  val results = List(("1x1", Miss), ("2x2", Hit), ("2x3", Kill))

  "ships killed" should "be updated properly after ship's killing" in {
    val hexEnemyBoard = new HexEnemyBoard

    hexEnemyBoard.shipsKilled should be(0)

    hexEnemyBoard.processShotResults(results)

    hexEnemyBoard.shipsKilled should be(1)
  }

  "processShotResults" should "update board properly" in {
    val b = board
    val hexEnemyBoard = new HexEnemyBoard
    hexEnemyBoard.board should be(b.map(_.toVector).toVector)
    hexEnemyBoard.processShotResults(results)
    List(((1, 1), "-"), ((2, 2), "X"), ((2, 3), "X")).foreach { p =>
      b.update(p._1._1, b(p._1._1).updated(p._1._2, p._2))
    }
    b.map(_.toVector).toVector zip hexEnemyBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "board" should "not contain alive ships (*)" in {
    val hexEnemyBoard = new HexEnemyBoard
    hexEnemyBoard.board.flatten.contains("*") should be(false)
  }

}
