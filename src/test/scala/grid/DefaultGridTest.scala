package grid


import grid.Grid.ShotResult._
import org.scalatest._

class DefaultGridTest extends FlatSpec with Matchers {

  val ships = Set(Set((1, 1), (2, 2)), Set((3, 3)), Set((15, 15)))

  def grid = new DefaultGrid(ships, 16, 16)

  "Shoot" should "return hit" in {
    val salvo = List((2, 2))
    val res = grid.shoot(salvo)
    res.head._2 should be(Hit)
  }

  "Shoot" should "return kill" in {
    val salvo = List((1, 1), (2, 2), (2, 3), (3, 3))
    val res = grid.shoot(salvo)
    res(1)._2 should be(Kill)
  }

  "Shoot" should "return miss" in {
    val salvo = List((2, 3))
    val res = grid.shoot(salvo)
    res.head._2 should be(Miss)
  }

  "Shoot" should "return miss when was already hit" in {
    val g = grid
    val res1 = g.shoot(List((2, 2)))
    res1.head._2 should be(Hit)

    val res2 = g.shoot(List((2, 2)))
    res2.head._2 should be(Miss)

    val res3 = grid.shoot(List((2, 2), (2, 2)))
    res3.head._2 should be(Hit)
    res3(1)._2 should be(Miss)
  }


  "Shoot" should "return miss when was already killed" in {
    val g = grid
    val res1 = g.shoot(List((15, 15)))
    res1.head._2 should be(Kill)

    val res2 = g.shoot(List((15, 15)))
    res2.head._2 should be(Miss)

    val res3 = grid.shoot(List((15, 15), (15, 15)))
    res3.head._2 should be(Kill)
    res3(1)._2 should be(Miss)
  }

  "Ships" should "be the same after miss" in {
    val g = grid
    val salvo = List((2, 3))
    val res = g.shoot(salvo)
    res.head._2 should be(Miss)
    g.ships should be(ships)
  }

  "Ships" should "be updated after hit" in {
    val g = grid
    val salvo = List((1, 1))
    val res = g.shoot(salvo)
    res.head._2 should be(Hit)
    g.ships should be(ships.map(_ - salvo.head))
  }

  "Ships" should "be updated after kill" in {
    val g = grid
    val salvo = List((15, 15))
    val res = g.shoot(salvo)
    res.head._2 should be(Kill)
    g.ships should be(ships.map(_ - salvo.head).filterNot(_.isEmpty))
  }

  "Ships beyond borders" should "throw GridInitException" in {
    intercept[GridInitException]{
      val ships = Set(Set((1, 1), (2, 2)), Set((3, 3)), Set((16, 15)))
      new DefaultGrid(ships, 16, 16)
    }
  }
}
