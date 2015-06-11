package grid

trait Grid {
  import Grid._
  import Grid.ShotResult.ShotResult

  def size: (Int, Int)
  def ships: Set[Ship]
  def shoot(salvo: List[Shot]): List[(Shot, ShotResult)]
}

object Grid {
  type Shot = (Int, Int)
  type Ship = Set[(Int, Int)]

  object ShotResult extends Enumeration {
    type ShotResult = Value
    val Hit, Kill, Miss = Value
  }
}

class GridInitException(mes: String) extends Exception(mes)