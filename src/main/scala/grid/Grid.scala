package grid

trait Grid {
  import Grid.ShotResult.ShotResult
  import Grid._

  def size: (Int, Int)
  def ships: Set[Ship]
  def shoot(salvo: List[Shot]): List[(Shot, ShotResult)]
  def missed: Set[Shot]
  def hit: Set[Shot]
}

object Grid {
  type Shot = (Int, Int)
  type Ship = Set[(Int, Int)]

  object ShotResult extends Enumeration {
    type ShotResult = Value
    val Hit, Kill, Miss = Value
  }
}
