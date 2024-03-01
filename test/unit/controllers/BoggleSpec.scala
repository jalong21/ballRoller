//package unit.controllers
//
//import akka.stream.Materializer
//import akka.util.Timeout
//import controllers.BoggleController
//import org.scalatestplus.mockito.MockitoSugar
//import org.scalatestplus.play._
//import org.scalatestplus.play.guice.GuiceOneAppPerSuite
//import play.api.test.Helpers.POST
//import play.api.test._
//
//import scala.concurrent.duration.DurationInt
//
//class BoggleSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {
//
//  implicit lazy val materializer: Materializer = app.materializer
//  implicit val timeout: Timeout = Timeout(5.seconds)
//
//  "TumbleController" should {
//    "return an error when the body is missing" in {
//
//      val request = FakeRequest(POST, "/v1/initializeTumble")
//      val tumbleController = new BoggleController(Helpers.stubControllerComponents())
//    }
//  }
//}
