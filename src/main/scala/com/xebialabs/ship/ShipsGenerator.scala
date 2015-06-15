package com.xebialabs.ship

import com.xebialabs.grid.Grid.Ship

import scala.util.Random

trait ShipsGenerator {
  def generateShips: Set[Ship]
}

class DefaultShipsGenerator(size: (Int, Int)) extends ShipsGenerator {
  private val types = List(Winger, Angle, AClass, BClass, SClass)

  def generateShips: Set[Ship] = {
    val r = Random
    def place(k: Int, acc: Set[Ship]): Set[Ship] = {
      if (k >= types.size) acc
      else {
        val x = r.nextInt(size._1)
        val y = r.nextInt(size._1)
        val rots = rotations(types(k)(x, y)).filter(isSafe(_, size, acc.flatten))
        r.shuffle(rots).headOption match {
          case Some(z) => place(k + 1, acc + z)
          case None => place(k, acc)
        }
      }
    }
    place(0, Set())
  }

  private def isSafe(s: Ship, size: (Int, Int), taken: Set[(Int, Int)] = Set()) = {
    val rows = size._1
    val columns = size._2
    s.forall(x => x._1 >= 0 && x._1 < rows && x._2 >= 0 && x._2 < columns && !taken.contains(x))
  }


  private def rotations(s: Spaceship) = Set(s.toUp, s.toDown, s.toLeft, s.toRight)

}