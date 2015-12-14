package com.landoop.avro4sui

import java.util.Scanner
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}

trait HodorEndpoints {

  def server(port: Int): Unit = {
    println(s"Starting alerting on $port")

    val handler = new ServletContextHandler
    handler.setContextPath("/")

    val holder = new ServletHolder(new HodorServlet)
    holder.setAsyncSupported(true)
    handler.addServlet(holder, "/avro2scala")

    val server = new Server(port)
    server.setHandler(handler)
    server.start()

    println(s"Avro2Scala converter listening on port: $port")
  }
}

class HodorServlet extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    if (req.getMethod.equalsIgnoreCase("POST")) {
      val postRequestObject = new Scanner(req.getInputStream, "UTF-8").useDelimiter("\\A").nextLine
      println("Detected POST with payload:" + postRequestObject)

      try {
        val incomingSchema = new org.apache.avro.Schema.Parser().parse(postRequestObject)
        println(incomingSchema)
      } catch {
        case (e:Throwable) => println(e)
      }

    }

//    val schema = new JsonToAvroConverter("com.test.avro").convert("MyClass", json)
//    println(schema.toString(true))

    resp.setContentType("application/json")
    resp.getWriter.write("schema")
  }
}

object App extends HodorEndpoints with App {

  case class Testing(name: String, description: String)

  println("Visit me at   http://localhost:1082/avro2scala/ ")
  println("Test me with  curl -X POST --data '123' http://localhost:1082/avro2scala")
  server(1082)
}
