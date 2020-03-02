package controllers

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

import akka.stream.Materializer
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.Offers._
import models.Responses.CreateOfferResponse
import org.mockito.Mockito._
import org.scalatest.{EitherValues, Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice._
import play.api.mvc.Headers
import play.api.test.Helpers._
import play.api.test._
import services.OfferService

import scala.concurrent.Future

class OfferControllerSpec extends WordSpec with Matchers with GuiceOneAppPerTest with MockitoSugar with EitherValues {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  "OfferController" should {
    "create an offer" in new TestUtils {

      val offerId = OfferId(UUID.randomUUID())
      val offer = Offer("xyz", Product("abc"), Instant.now.plus(5, ChronoUnit.DAYS))
      val payload = offer.asJson.toString
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json"))).withBody(payload)

      val offerWithId = OfferWithId(offerId, offer)
      when(offerService.addOffer(offer)).thenReturn(Future.successful(Some(offerWithId)))

      val controller = new OfferController(stubControllerComponents(), offerService)

      val createdOffer = call(controller.create, request)

      parse(contentAsString(createdOffer)).flatMap(_.as[CreateOfferResponse]) shouldBe Right(CreateOfferResponse(offerWithId = offerWithId))

      status(createdOffer) shouldBe CREATED
    }

  }

  trait TestUtils {
    implicit lazy val materializer: Materializer = app.materializer

    val offerService = mock[OfferService]

  }

}


