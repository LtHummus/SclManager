package com.lthummus.sclmanager

import org.scalatra._

trait SclManagerStack extends ScalatraServlet {

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
  }

}
