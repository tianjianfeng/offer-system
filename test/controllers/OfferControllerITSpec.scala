package controllers

import akka.stream.Materializer
import models.Offers.Offer
import org.scalatest.{EitherValues, Matchers, WordSpec}
import org.scalatestplus.play.guice._
import play.api.mvc.Headers
import play.api.test.Helpers._
import play.api.test._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.Responses.CreateOfferResponse

class OfferControllerITSpec extends WordSpec with Matchers with GuiceOneAppPerTest with EitherValues{

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit lazy val materializer: Materializer = app.materializer

  "OfferController" should {
    "create an offer" in  {
      val offer = Offer(description = "abc")
      val payload = offer.asJson.toString
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json"))).withBody(payload)
      val Some(result) = route(app, request)
      status(result) shouldBe CREATED

      parse(contentAsString(result)).flatMap(_.as[CreateOfferResponse]) shouldBe Right(CreateOfferResponse(offer))

    }
  }
}


