package com.xebialabs.board

import com.xebialabs.exception.HexConversionException

trait HexConversions {

  def hexToShot(hex: String): (Int, Int) = {
    def hexToInt(hex: String): Int = hex.toUpperCase match {
      case "A" => 10
      case "B" => 11
      case "C" => 12
      case "D" => 13
      case "E" => 14
      case "F" => 15
      case x if x.toInt > 9 || x.toInt < 0 => throw new HexConversionException("Wrong hex coordinates: " + x)
      case x => x.toInt
    }
    val a = hex.split("x").map(hexToInt)
    (a(0), a(1))
  }

  def shotToHex(shot: (Int, Int)): String = {
    intToHex(shot._1) + "x" + intToHex(shot._2)
  }

  def intToHex(i: Int): String = i match {
    case 10 => "A"
    case 11 => "B"
    case 12 => "C"
    case 13 => "D"
    case 14 => "E"
    case 15 => "F"
    case x if x >= 0 && x <= 9 => x.toString
    case x => throw new HexConversionException(s"Can't convert $x into hex")
  }

}