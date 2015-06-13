package grid

import grid.Grid._

class HexGrid(ships: Set[Ship]) extends DefaultGrid(ships, 16, 16)