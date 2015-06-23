package com.xebialabs.ship

import com.xebialabs.grid.Grid._

trait ShipsUtils {

  val types = List(Winger, Angle, AClass, BClass, SClass)

  def isSafe(s: Ship, size: (Int, Int), taken: Set[(Int, Int)] = Set()) = {
    val rows = size._1
    val columns = size._2
    s.forall(x => x._1 >= 0 && x._1 < rows && x._2 >= 0 && x._2 < columns && !taken.contains(x))
  }

  def rotations(s: Spaceship) = Set(s.toUp, s.toDown, s.toLeft, s.toRight)
}
