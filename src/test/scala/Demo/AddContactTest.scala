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

  def login() = {
  exec(http("login")
    .post("/users/login")
    .body(StringBody(
      s"""
         |{
         |  "email": "$email",
         |  "password": "$password"
         |}
         |""".stripMargin)).asJson
    .check(jsonPath("$.token").saveAs("authToken"))
  )
}

  // 2 Scenario Definition
  val scn = scenario("contact")
    .exec(login()) // Llamar al login para obtener el token
    .exec(http("contact")
      .post(s"/contacts")
      .header("Authorization", "Bearer ${authToken}") // Usar el token guardado en la variable
      .body(StringBody(
        s"""
           |{
           |  "firstName": "$firstName",
           |  "lastName": "$lastName",
           |  "birthdate": "$birthDate",
           |  "email": "$contactEmail",
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
    scn.inject(rampUsersPerSec(5).to(15).during(30))
  ).protocols(httpConf);
}