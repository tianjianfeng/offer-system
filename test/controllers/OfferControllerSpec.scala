package controllers

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

import akka.stream.Materializer
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.Offers._
import models.Responses._
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
    "Call to create an offer successfully" in new TestUtils {

      val offerId = OfferId(UUID.randomUUID())
      val offer = Offer("xyz", Product("abc"), Instant.now.plus(1, ChronoUnit.HALF_DAYS).plus(1, ChronoUnit.SECONDS), 2.0f)
      val payload = offer.asJson.toString
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json"))).withBody(payload)

      val offerWithId = OfferWithId(offerId, offer)
      when(offerService.addOffer(offer)).thenReturn(Future.successful(Some(offerWithId)))

      val controller = new OfferController(stubControllerComponents(), offerService)

      val createdOffer = call(controller.create, request)

      parse(contentAsString(createdOffer)).flatMap(_.as[CreateOfferResponse]) shouldBe Right(CreateOfferResponse(offerWithId = offerWithId))

      status(createdOffer) shouldBe CREATED
    }

    "Call to create an offer with invalid expireDate" in new TestUtils {

      val offerId = OfferId(UUID.randomUUID())
      val offer = Offer("xyz", Product("abc"), Instant.now.plus(1, ChronoUnit.HALF_DAYS).minus(1, ChronoUnit.SECONDS), 2.0f)
      val payload = offer.asJson.toString
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json"))).withBody(payload)

      val offerWithId = OfferWithId(offerId, offer)

      val controller = new OfferController(stubControllerComponents(), offerService)

      val createdOffer = call(controller.create, request)

      status(createdOffer) shouldBe BAD_REQUEST
      parse(contentAsString(createdOffer)).flatMap(_.as[CreateOfferClientErrorResponse]) shouldBe Right(CreateOfferClientErrorResponse(offer = offer))
    }

    "Call to create an offer but repo failed to add it" in new TestUtils {

      val offerId = OfferId(UUID.randomUUID())
      val offer = Offer("xyz", Product("abc"), Instant.now.plus(5, ChronoUnit.DAYS), 2.0f)
      val payload = offer.asJson.toString
      val request = FakeRequest(POST, "/offers").withHeaders(Headers(("Content-Type", "application/json"))).withBody(payload)

      val offerWithId = OfferWithId(offerId, offer)
      when(offerService.addOffer(offer)).thenReturn(Future.successful(None))

      val controller = new OfferController(stubControllerComponents(), offerService)

      val createdOffer = call(controller.create, request)

      parse(contentAsString(createdOffer)).flatMap(_.as[CreateOfferFailureResponse]) shouldBe Right(CreateOfferFailureResponse(offer = offer))

      status(createdOffer) shouldBe INTERNAL_SERVER_ERROR
    }

    "Call to get an offer and find it" in new TestUtils {
      val offerId = UUID.randomUUID()
      val offer = Offer("xyz", Product("abc"), Instant.now.plus(5, ChronoUnit.DAYS), 2.0f)
      val offerWithId = OfferWithId(OfferId(offerId), offer)

      when(offerService.getOffer(OfferId(offerId))).thenReturn(Future.successful(Some(OfferWithId(OfferId(offerId), offer))))
      val request = FakeRequest(GET, s"/offers/${offerId.toString}")

      val controller = new OfferController(stubControllerComponents(), offerService)

      val result = call(controller.get(offerId), request)

      status(result) shouldBe OK
      parse(contentAsString(result)).flatMap(_.as[FoundOfferResponse]) shouldBe Right(FoundOfferResponse(offerWithId = offerWithId))
    }

    "Call to get an offer but not found" in new TestUtils {
      val offerId = UUID.randomUUID()
      val offer = Offer("xyz", Product("abc"), Instant.now.plus(5, ChronoUnit.DAYS), 2.0f)
      val offerWithId = OfferWithId(OfferId(offerId), offer)

      when(offerService.getOffer(OfferId(offerId))).thenReturn(Future.successful(None))
      val request = FakeRequest(GET, s"/offers/${offerId.toString}")

      val controller = new OfferController(stubControllerComponents(), offerService)

      val result = call(controller.get(offerId), request)

      status(result) shouldBe NOT_FOUND
      parse(contentAsString(result)).flatMap(_.as[OfferNotFoundResponse]) shouldBe Right(OfferNotFoundResponse(offerId = OfferId(offerId)))
    }

    "Call to get an offer but expired" in new TestUtils {
      val offerId = UUID.randomUUID()
      val offer = Offer("xyz", Product("abc"), Instant.now.minus(1, ChronoUnit.SECONDS), 2.0f)
      val offerWithId = OfferWithId(OfferId(offerId), offer)

      when(offerService.getOffer(OfferId(offerId))).thenReturn(Future.successful(Some(OfferWithId(OfferId(offerId), offer))))
      val request = FakeRequest(GET, s"/offers/${offerId.toString}")

      val controller = new OfferController(stubControllerComponents(), offerService)

      val result = call(controller.get(offerId), request)

      status(result) shouldBe NOT_FOUND
      parse(contentAsString(result)).flatMap(_.as[OfferNotFoundResponse]) shouldBe Right(OfferNotFoundResponse(offerId = OfferId(offerId)))
    }

    "Call remove an offer but not found" in new TestUtils {
      val offerId = UUID.randomUUID()
      val offer = Offer("xyz", Product("abc"), Instant.now.minus(1, ChronoUnit.SECONDS), 2.0f)
      val offerWithId = OfferWithId(OfferId(offerId), offer)


      when(offerService.removeOffer(OfferId(offerId))).thenReturn(Future.successful(None))
      val request = FakeRequest(DELETE, s"/offers/${offerId.toString}")

      val controller = new OfferController(stubControllerComponents(), offerService)
      val removedOffer = call(controller.remove(offerId), request)

      status(removedOffer) shouldBe NOT_FOUND
      parse(contentAsString(removedOffer)).flatMap(_.as[OfferNotFoundResponse]) shouldBe Right(OfferNotFoundResponse(offerId = OfferId(offerId)))
    }

  }

  trait TestUtils {
    implicit lazy val materializer: Materializer = app.materializer

    val offerService = mock[OfferService]

  }

}


