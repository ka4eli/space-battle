package board

import exception.HexConversionException

trait HexConversions {
  type Hex = String

  def hexToShot(hex: Hex): (Int, Int) = {
    def hexToInt(hex: Hex): Int = hex.toUpperCase match {
      case "A" => 10
      case "B" => 11
      case "C" => 12
      case "D" => 13
      case "E" => 14
      case "F" => 15
      case x if x.toInt > 15 || x.toInt < 0 => throw new HexConversionException("Wrong hex coordinates: " + x)
      case x => x.toInt
    }
    val a = hex.split("x").map(hexToInt)
    (a(0), a(1))
  }

  def shotToHex(shot: (Int, Int)): Hex = {
    def intToHex(i: Int): Hex = i match {
      case 10 => "A"
      case 11 => "B"
      case 12 => "C"
      case 13 => "D"
      case 14 => "E"
      case 15 => "F"
      case _ => i.toString
    }
    intToHex(shot._1) + "x" + intToHex(shot._2)
  }

}