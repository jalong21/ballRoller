package unit.controllers

import akka.stream.Materializer
import akka.util.Timeout
import controllers.Health
import org.scalatest.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.OK
import play.api.test.Helpers.{POST, status}
import play.api.test._

import scala.concurrent.duration.DurationInt

class HealthSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  implicit val timeout: Timeout = Timeout(5.seconds)

  "HealthController" must {
    "return true" in {

      val request = FakeRequest(POST, "/v1/initializeTumble")
      val healthController = new Health(Helpers.stubControllerComponents())

      val result = healthController.index()(request)
      status(result) shouldBe OK
    }
  }
}
