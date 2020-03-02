package controllers

import akka.stream.Materializer
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice._
import play.api.mvc.Headers
import play.api.test.Helpers._
import play.api.test._

class OfferControllerITSpec extends WordSpec with Matchers with GuiceOneAppPerTest {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit lazy val materializer: Materializer = app.materializer

  "OfferController" should {
    "create an offer" in  {
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json")))
      val Some(result) = route(app, request)
      status(result) shouldBe CREATED
    }
  }
}


