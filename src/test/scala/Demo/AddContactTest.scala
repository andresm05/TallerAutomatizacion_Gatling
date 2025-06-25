package Demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Demo.Data._
import scala.util.Random

class AddContactTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    //Verificar de forma general para todas las solicitudes
    .check(status.is(200))

    // Generar email aleatorio
  def randomEmail(): String = {
    val prefix = Random.alphanumeric.filter(_.isLetter).take(8).mkString
    s"$prefix@gmail.com"
  }

  // 2 Scenario Definition
  val scn = scenario("contact").
    .exec { session =>
      session.set("email", randomEmail())
    }
    .exec(http("contact")
      .post(s"/contacts")
      .header("Authorization", "Bearer ${authToken}") // Usar el token guardado en la variable
      .body(StringBody(
        s"""
           |{
           |  "firstName": "$firstName",
           |  "lastName": "$lastName",
           |  "birthDate": "$birthDate",
           |  "email": "${email}",
           |  "phone": "$phone",
           |  "street1": "$street1",
           |  "street2": "$street2",
           |  "city": "$city",
           |  "stateProvince": "$stateProvince",
           |  "postalCode": "$postalCode",
           |  "country": "$country"
              |}
              |""".stripMargin)).asJson
        .check(status.is(201)) // Verificar que la respuesta sea 201 Created
    )

  // 3 Load Scenario
  setUp(
    scn.inject(atOnceUsers(50))
  ).protocols(httpConf);
}