package grid

import exception.GridInitException
import grid.Grid.ShotResult._
import grid.Grid.{Ship, Shot}

class DefaultGrid(shipsSet: Set[Ship], rows: Int, columns: Int) extends Grid {
  val size = (rows, columns)

  private var _ships = shipsSet
  private var hitSet = Set.empty[Shot]
  private var missedSet = Set.empty[Shot]

  def hit = hitSet

  def missed = missedSet

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