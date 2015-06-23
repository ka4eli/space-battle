package com.xebialabs.ship

import com.xebialabs.grid.Grid.Ship

import scala.util.Random

trait ShipsGenerator {
  def generateShips: Set[Ship]
}

class DefaultShipsGenerator(size: (Int, Int)) extends ShipsGenerator with ShipsUtils {

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

}

object HexShipGenerator extends DefaultShipsGenerator(16, 16)