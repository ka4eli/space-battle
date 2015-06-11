package grid

import grid.Grid.Shot
import grid.Grid.Ship
import grid.Grid.ShotResult._

class DefaultGrid(var ships: Set[Ship], rows: Int, columns: Int) extends Grid {
  val size = (rows, columns)

  if (!ships.forall(_.forall(x => x._1 >= 0 && x._1 < rows && x._2 >= 0 && x._2 < columns))) {
    throw new GridInitException(s"Can't accommodate ships $ships on grid with size $size")
  }

  def shoot(salvo: List[Shot]): List[(Shot, ShotResult)] = salvo.flatMap { shot =>
    ships.filter(_.contains(shot)) map { ship =>
      val newShip = ship - shot
      val shotResult =
        if ((newShip.size < ship.size) && newShip.isEmpty) {
          ships = ships.filter(!_.contains(shot))
          Kill
        }
        else {
          ships = ships.filter(!_.contains(shot)) + newShip
          Hit
        }
      (shot, shotResult)
    } match {
      case s: Set[_] if s.isEmpty => Set((shot, Miss))
      case s => s
    }
  }

}