package com.xebialabs.ship

import com.xebialabs.grid.Grid._

trait Spaceship {

  def toUp: Ship

  def toDown: Ship

  def toLeft: Ship

  def toRight: Ship
}

case class Winger(start: (Int, Int)) extends Spaceship {
  private val x = start._1
  private val y = start._2

  def toUp: Ship = Set(x, x + 1, x + 3, x + 4).flatMap(i => Set((i, y), (i, y + 2))) + ((x + 2, y + 1))

  def toDown: Ship = Set(x, x - 1, x - 3, x - 4).flatMap(i => Set((i, y), (i, y - 2))) + ((x - 2, y - 1))

  def toLeft: Ship = Set(y, y + 1, y + 3, y + 4).flatMap(i => Set((x, i), (x - 2, i))) + ((x - 1, y + 2))

  def toRight: Ship = Set(y, y - 1, y - 3, y - 4).flatMap(i => Set((x, i), (x + 2, i))) + ((x + 1, y - 2))

}

case class Angle(start: (Int, Int)) extends Spaceship {
  private val x = start._1
  private val y = start._2

  def toUp: Ship = Set(x, x + 1, x + 2, x + 3).map((_, y)) ++ Set(y, y + 1, y + 2).map((x + 3, _))

  def toDown: Ship = Set(x, x - 1, x - 2, x - 3).map((_, y)) ++ Set(y, y - 1, y - 2).map((x - 3, _))

  def toLeft: Ship = Set(y, y + 1, y + 2, y + 3).map((x, _)) ++ Set(x, x - 1, x - 2).map((_, y + 3))

  def toRight: Ship = Set(y, y - 1, y - 2, y - 3).map((x, _)) ++ Set(x, x + 1, x + 2).map((_, y - 3))

}

case class AClass(start: (Int, Int)) extends Spaceship {
  private val x = start._1
  private val y = start._2

  def toUp: Ship = Set(x + 1, x + 2, x + 3).flatMap(i => Set((i, y - 1), (i, y + 1))) + start + ((x + 2, y))

  def toDown: Ship = Set(x - 1, x - 2, x - 3).flatMap(i => Set((i, y - 1), (i, y + 1))) + start + ((x - 2, y))

  def toLeft: Ship = Set(y + 1, y + 2, y + 3).flatMap(i => Set((x - 1, i), (x + 1, i))) + start + ((x, y + 2))

  def toRight: Ship = Set(y - 1, y - 2, y - 3).flatMap(i => Set((x - 1, i), (x + 1, i))) + start + ((x, y - 2))
}

case class BClass(start: (Int, Int)) extends Spaceship {
  private val x = start._1
  private val y = start._2

  def toUp: Ship = Set(x - 2, x - 1, x, x + 1, x + 2).map((_, y - 1)) ++ Set(x - 2, x, x + 2).map((_, y)) ++ Set(x - 1, x + 1).map((_, y + 1))

  def toDown: Ship = Set(x - 2, x - 1, x, x + 1, x + 2).map((_, y + 1)) ++ Set(x - 2, x, x + 2).map((_, y)) ++ Set(x - 1, x + 1).map((_, y - 1))

  def toLeft: Ship = Set(y - 2, y - 1, y, y + 1, y + 2).map((x + 1, _)) ++ Set(y - 2, y, y + 2).map((x, _)) ++ Set(y - 1, y + 1).map((x - 1, _))

  def toRight: Ship = Set(y - 2, y - 1, y, y + 1, y + 2).map((x - 1, _)) ++ Set(y - 2, y, y + 2).map((x, _)) ++ Set(y - 1, y + 1).map((x + 1, _))
}


case class SClass(start: (Int, Int)) extends Spaceship {
  private val x = start._1
  private val y = start._2

  def toUp: Ship = Set(x - 2, x, x + 2).flatMap(i => Set((i, y), (i, y + 1))) ++ Set((x - 1, y - 1), (x + 1, y + 2))

  def toDown: Ship = Set(x - 2, x, x + 2).flatMap(i => Set((i, y), (i, y - 1))) ++ Set((x + 1, y + 1), (x - 1, y - 2))

  def toLeft: Ship = Set(y - 2, y, y + 2).flatMap(i => Set((x, i), (x - 1, i))) ++ Set((x + 1, y - 1), (x - 2, y + 1))

  def toRight: Ship = Set(y - 2, y, y + 2).flatMap(i => Set((x, i), (x + 1, i))) ++ Set((x - 1, y + 1), (x + 2, y - 1))
}


