package com.lthummus.sclmanager

import org.jooq.DSLContext
import org.scalatra._

class SclManagerServlet(implicit dslContext: DSLContext) extends SclManagerStack {

  get("/") {
    Ok("nothing")
  }

}
