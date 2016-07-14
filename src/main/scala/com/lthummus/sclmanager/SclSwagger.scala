package com.lthummus.sclmanager

import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, JacksonSwaggerBase, Swagger}

class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with JacksonSwaggerBase

object SclApiInfo extends ApiInfo (
  "SclManager",
  "Docs for SCL Manager API",
  "http://github.com/LtHummus/SclManager",
  "none@example.com",
  "Unknown",
  "http://example.com"
)

class SclSwagger extends Swagger(Swagger.SpecVersion, "1.0.0", SclApiInfo)
