package com.xebialabs.controller

import com.xebialabs.json.ShootResultJsonSerializer
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.scalatra.json.JacksonJsonSupport

trait JsonSupport extends JacksonJsonSupport {
  protected implicit val jsonFormats = DefaultFormats.+(new ShootResultJsonSerializer)

  before() {
    contentType = formats("json")
  }

  protected override def transformResponseBody(body: JValue): JValue = body.underscoreKeys

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys
}
