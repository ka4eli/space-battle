package processing

import grid.Grid.Ship

trait ShipsGenerator {

  def generateShips: Set[Ship] = Set()
}
