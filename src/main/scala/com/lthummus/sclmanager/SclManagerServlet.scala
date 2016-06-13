package com.lthummus.sclmanager

import org.scalatra._

class SclManagerServlet extends SclManagerStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
