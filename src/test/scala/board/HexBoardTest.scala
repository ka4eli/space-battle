package board

import grid.DefaultGrid
import org.scalatest._
import grid.Grid.ShotResult._
import scala.collection.mutable.ArrayBuffer

class HexBoardTest extends FlatSpec with Matchers {
  val ships = Set(Set((1, 1), (2, 2)), Set((3, 3)), Set((15, 15)))
  val grid = new DefaultGrid(ships, 16, 16)

  def boardArray = {
    val board = ArrayBuffer.fill(grid.size._1)(ArrayBuffer.fill(grid.size._2)("."))
    board(1)(1) = "*"
    board(2)(2) = "*"
    board(3)(3) = "*"
    board(15)(15) = "*"
    board
  }

  "board" should "be constructed from ships properly" in {
    val hexBoard = new HexBoard(grid)
    boardArray.map(_.toVector).toVector zip hexBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "processShoot" should "update board properly after kill" in {
    val hexBoard = new HexBoard(grid)
    val salvo = List("FxF")
    val res = hexBoard.processShoot(salvo)
    res.head._2 should be(Kill)

    boardArray.updated(15, boardArray(15).updated(15, "X")).map(_.toVector).toVector zip hexBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "processShoot" should "update board properly after hit" in {
    val hexBoard = new HexBoard(grid)
    val salvo = List("1x1")
    val res = hexBoard.processShoot(salvo)
    res.head._2 should be(Hit)

    boardArray.updated(1, boardArray(1).updated(1, "X")).map(_.toVector).toVector zip hexBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "processShoot" should "throw NumberFormatException" in {
    val hexBoard = new HexBoard(grid)
    val salvo = List("FxZ")
    println(hexBoard.board2String)

    intercept[NumberFormatException] {
      hexBoard.processShoot(salvo)
    }
  }

  "processShoot" should "throw HexBoardCoordinateException" in {
    val hexBoard = new HexBoard(grid)
    val salvo = List("Fx16")

    intercept[HexBoardCoordinateException] {
      hexBoard.processShoot(salvo)
    }
  }

  "enemy board" should "not contain alive ships (*)" in {
    val hexBoard = new HexBoard(grid)
    hexBoard.enemyBoard.flatten.contains("*") should be(false)
  }

  "player board" should "not contain missed shots (-) and hits (X)" in {
    val hexBoard = new HexBoard(grid)
    val salvo = List("1x1", "2x2", "2x3", "3x3", "FxF")

    val res = hexBoard.processShoot(salvo)
    res.filter(_._2 == Hit) should not be empty
    res.filter(_._2 == Kill) should not be empty
    res.filter(_._2 == Miss) should not be empty

    hexBoard.playerBoard.flatten.forall(s => s != "-" && s != "X") should be(true)
  }

}
