package com.landoop.avro4sui

import java.util.Scanner
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import com.sksamuel.avro4s.{Template, TemplateGenerator, ModuleGenerator}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}

trait Avro4sEndpoint {

  def server(port: Int): Unit = {
    println(s"Starting alerting on $port")

    val handler = new ServletContextHandler
    handler.setContextPath("/")

    // Avro => Scala Case Class
    val avro2scala = new ServletHolder(new Avro2CaseClassServlet )
    avro2scala.setAsyncSupported(true)
    handler.addServlet(avro2scala, "/avro2scala")

    // Json => Avro
    val json2avro = new ServletHolder(new Json2AvroServlet )
    json2avro.setAsyncSupported(true)
    handler.addServlet(json2avro, "/json2avro")

    val server = new Server(port)
    server.setHandler(handler)
    server.start()

    println(s"Avro2Scala converter listening on port: $port")
  }
}

class Avro2CaseClassServlet extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    if (req.getMethod.equalsIgnoreCase("POST")) {
      val postRequestObject = new Scanner(req.getInputStream, "UTF-8").useDelimiter("\\A").nextLine
      println("Detected POST with payload:" + postRequestObject)
      try {
        val modules = ModuleGenerator(postRequestObject)
        val templates = TemplateGenerator(modules)
        templates.foreach(t => println(t.definition + " ---- " + t.file))
        resp.setContentType("text/html; charset=utf-8")
        resp.getWriter.write(templates.head.definition)
      } catch {
        case (e:Throwable) => println(e)
      }
    }
  }
}

class Json2AvroServlet extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    if (req.getMethod.equalsIgnoreCase("POST")) {
      val postRequestObject = new Scanner(req.getInputStream, "UTF-8").useDelimiter("\\A").nextLine
      println("Detected POST with payload:" + postRequestObject)
      try {
//        val schema = JsonToAvroConverter("com.test.avro").convert("MyClass", "json")
//        println(schema.toString(true))

        val modules = ModuleGenerator(postRequestObject)
        val templates = TemplateGenerator(modules)
        templates.foreach(t => println(t.definition + " ---- " + t.file))
        resp.setContentType("text/html; charset=utf-8")
        resp.getWriter.write(templates.head.definition)
      } catch {
        case (e:Throwable) => println(e)
      }
    }
  }
}

object App extends Avro4sEndpoint with App {

  case class Testing(name: String, description: String)

  println("Visit me at   http://localhost:1082/avro2scala/ ")
  println("""Test me with  curl -X POST --data '{ "type" : "record", "name" : "MyClass", "namespace" : "com.test.avro", "fields" : [ { "name" : "foo", "type" : { "type" : "array", "items" : "boolean" } } ] }' http://localhost:1082/avro2scala""")
  server(1082)
}
