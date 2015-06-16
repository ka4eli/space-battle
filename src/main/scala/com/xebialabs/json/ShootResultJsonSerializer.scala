package com.xebialabs.json

import com.xebialabs.grid.Grid.ShotResult
import org.json4s.reflect.TypeInfo
import org.json4s.{JsonDSL, Serializer, _}

class ShootResultJsonSerializer extends Serializer[ShotResult.ShotResult] {

  import JsonDSL._

  val clz = classOf[ShotResult.ShotResult]

  private[this] val states = Map[ShotResult.ShotResult, String](ShotResult.Hit -> "hit",
    ShotResult.Kill -> "kill", ShotResult.Miss -> "miss")

  private[this] def isValid(json: JValue) = json match {
    case JString(value) => states.values.toList.contains(value)
    case _ => false
  }

  def deserialize(implicit format: Formats):
  PartialFunction[(TypeInfo, JValue), ShotResult.ShotResult] = {
    case (TypeInfo(clz, _), json) if isValid(json) => json match {
      case JString(value) => states.map(_.swap).get(value).get
      case value => throw new MappingException(s"Can't convert $value to ShotResult")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case i: ShotResult.ShotResult => states(i)
  }
}