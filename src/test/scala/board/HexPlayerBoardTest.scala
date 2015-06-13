package board

import _root_.exceptions.HexConversionException
import grid.Grid.ShotResult._
import grid.HexGrid
import org.scalatest._

import scala.collection.mutable.ArrayBuffer

class HexPlayerBoardTest extends FlatSpec with Matchers {
  val ships = Set(Set((1, 1), (2, 2)), Set((3, 3)), Set((15, 15)))

  def grid = new HexGrid(ships)

  def boardArray = {
    val board = ArrayBuffer.fill(16)(ArrayBuffer.fill(16)("."))
    board(1)(1) = "*"
    board(2)(2) = "*"
    board(3)(3) = "*"
    board(15)(15) = "*"
    board
  }

  "board" should "be constructed from ships properly" in {
    val hexBoard = new HexPlayerBoard(grid)
    boardArray.map(_.toVector).toVector zip hexBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "processShoot" should "update board properly after kill" in {
    val hexBoard = new HexPlayerBoard(grid)
    hexBoard.board(15)(15) should be("*")

    val salvo = List("FxF")
    val res = hexBoard.processSalvo(salvo)
    res.head._2 should be(Kill)

    boardArray.updated(15, boardArray(15).updated(15, ".")).map(_.toVector).toVector zip hexBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "processShoot" should "update board properly after hit" in {
    val hexBoard = new HexPlayerBoard(grid)
    hexBoard.board(1)(1) should be("*")

    val salvo = List("1x1")
    val res = hexBoard.processSalvo(salvo)
    res.head._2 should be(Hit)
    boardArray.updated(1, boardArray(1).updated(1, ".")).map(_.toVector).toVector zip hexBoard.board foreach { p =>
      p._1 should be(p._2)
    }
  }

  "player board" should "not contain missed shots (-) and hits (X)" in {
    val hexBoard = new HexPlayerBoard(grid)
    val salvo = List("1x1", "2x2", "2x3", "3x3", "FxF")

    val res = hexBoard.processSalvo(salvo)
    res.filter(_._2 == Hit) should not be empty
    res.filter(_._2 == Kill) should not be empty
    res.filter(_._2 == Miss) should not be empty

    hexBoard.board.flatten.forall(s => s != "-" && s != "X") should be(true)
  }

  "processSalvo" should "throw HexConversionException" in {
    val hexBoard = new HexPlayerBoard(grid)
    val salvo = List("Fx16")

    intercept[HexConversionException] {
      hexBoard.processSalvo(salvo)
    }
  }

  "processSalvo" should "throw NumberFormatException" in {
    val hexBoard = new HexPlayerBoard(grid)
    val salvo = List("FxZ")

    intercept[NumberFormatException] {
      hexBoard.processSalvo(salvo)
    }
  }
}
