package controllers

import java.time.Instant
import java.time.temporal.ChronoUnit

import akka.stream.Materializer
import models.Offers.{Offer, Product}
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
    "an offer lifecycle" in  {
      val offer = Offer(description = "abc", product = Product("xyz"), expireDate = Instant.now.plus(5, ChronoUnit.DAYS))
      val payload = offer.asJson.toString

      // create an offer
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json"))).withBody(payload)
      val Some(result) = route(app, request)
      status(result) shouldBe CREATED

      parse(contentAsString(result)).flatMap(_.as[CreateOfferResponse].map(_.offerWithId.offer)) shouldBe Right(offer)

      val offerId = (contentAsJson(result) \ "offerWithId" \ "offerId" \ "value").as[String]

      // get the offer
      val getRequest = FakeRequest(GET, s"/offers/${offerId}")

      val Some(getResult) = route(app, getRequest)
      status(getResult) shouldBe OK

      // remove the  offer
      val removeRequest = FakeRequest(DELETE, s"/offers/${offerId}")
      val Some(removeResult ) = route(app, removeRequest)
      status(removeResult) shouldBe OK

      val Some(removeResult2 ) = route(app, removeRequest)
      status(removeResult2) shouldBe NOT_FOUND

    }
  }
}


