package com.landoop.avro4sui

import java.util.Scanner
import com.sksamuel.avro4s.json.JsonToAvroConverter
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import com.sksamuel.avro4s.{TemplateGenerator, ModuleGenerator}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}

trait Avro4sEndpoint {

  def server(port: Int): Unit = {
    println(s"Starting alerting on $port")

    val handler = new ServletContextHandler
    handler.setContextPath("/")

    // We try all possible transformations before failing
    val servletAvro4s = new ServletHolder(new Avro4sServlet )
    servletAvro4s.setAsyncSupported(true)
    handler.addServlet(servletAvro4s, "/avro4s")

    val server = new Server(port)
    server.setHandler(handler)
    server.start()

    println(s"Servlet for 'avro4s' convertions listening on port: $port")
  }
}

class Avro4sServlet extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    println("Servicing")
    if (req.getMethod.equalsIgnoreCase("OPTIONS")) {
      println("OPTIONS")
      resp.setContentType("text/plain")
      resp.setHeader("Access-Control-Allow-Origin", "*")
      resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS")
      resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key")
      resp.getWriter.write("")
    } else
    if (req.getMethod.equalsIgnoreCase("POST")) {
      println("POST")
      val postRequestObject = new Scanner(req.getInputStream, "UTF-8").nextLine
      println(postRequestObject )
      println("Detected POST with payload:" + postRequestObject)
      var response = ""
      try {
        val modules = ModuleGenerator(postRequestObject)
        val templates = TemplateGenerator(modules)
        templates.foreach(t => println(t.definition + " ---- " + t.file))
        response+="scala###" + templates.head.definition + "###"
        println("[Avro] detected !!");
      } catch {
        case (e:Throwable) => println("Avro [NOT] Detected"); println(e)
      }
      try {
        val schema = new JsonToAvroConverter("com.test.avro").convert("MyClass", postRequestObject)
        val schemaString = schema.toString(true)
        println(schemaString)
        response+="avro###" + schemaString + "###"
        println("[JSon] detected !!");
     } catch {
        case (e:Throwable) => println("JSon [NOT] Detected"); println(e)
      }
      resp.setContentType("text/plain")
      resp.setHeader("Access-Control-Allow-Origin", "*")
      resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS")
      resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key")
      resp.getWriter.write(response)
    }
  }
}

object App extends Avro4sEndpoint with App {

  println("Test Avro => Scala with:")
  println("""  curl -X POST --data '{ "type" : "record", "name" : "MyClass", "namespace" : "com.test.avro", "fields" : [ { "name" : "foo", "type" : { "type" : "array", "items" : "boolean" } } ] }' http://localhost:1082/avro4s""")
  println("Test Json => Avro with:")
  val exampleJson = """ {"menu": { "id": "file", "value": "File", "popup": { "menuitem": [ {"value": "New" }, {"value": "Open" }, {"value": "Close"} ] } }} """.filter(_ >= ' ')
  println(s"""  curl -X POST --data '$exampleJson' http://localhost:1082/avro4s""")

  server(1082)
}
