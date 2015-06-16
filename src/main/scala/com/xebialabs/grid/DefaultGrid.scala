package com.xebialabs.grid

import com.xebialabs.exception.GridInitException
import com.xebialabs.grid.Grid.ShotResult._
import com.xebialabs.grid.Grid.{Ship, Shot}

class DefaultGrid(shipsSet: Set[Ship], rows: Int, columns: Int) extends Grid {
  val size = (rows, columns)

  private var _ships = shipsSet
  private var hitSet = Set.empty[Shot]
  private var missedSet = Set.empty[Shot]
  private var _killed = 0

  def hit = hitSet

  def missed = missedSet

  def killed = _killed

  def ships = _ships

  if (!_ships.forall(_.forall(x => x._1 >= 0 && x._1 < rows && x._2 >= 0 && x._2 < columns))) {
    throw new GridInitException(s"Can't accommodate ships ${_ships} on grid with size $size")
  }

  def shoot(salvo: List[Shot]): List[(Shot, ShotResult)] = salvo.flatMap { shot =>
    _ships.filter(_.contains(shot)) map { ship =>
      val newShip = ship - shot
      val shotResult =
        if ((newShip.size < ship.size) && newShip.isEmpty) {
          _ships = _ships.filter(!_.contains(shot))
          hitSet += shot
          _killed += 1
          Kill
        }
        else {
          _ships = _ships.filter(!_.contains(shot)) + newShip
          hitSet += shot
          Hit
        }
      (shot, shotResult)
    } match {
      case s: Set[_] if s.isEmpty =>
        missedSet += shot
        Set((shot, Miss))
      case s => s
    }
  }

}