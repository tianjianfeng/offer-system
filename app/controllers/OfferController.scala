package controllers

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

import io.circe.generic.auto._
import io.circe.syntax._
import javax.inject.Inject
import models.Offers.{Offer, OfferId}
import models.Responses._
import play.api.libs.circe.Circe
import play.api.mvc._
import services.OfferService

import scala.concurrent.{ExecutionContext, Future}

class OfferController @Inject()(val cc: ControllerComponents, offerService: OfferService)(implicit exec: ExecutionContext)  extends AbstractController(cc) with Circe {

  def create = Action.async(circe.json[Offer]) { implicit request =>
    val offer = request.body

    if (offer.expireDate.compareTo(Instant.now.plus(1, ChronoUnit.HALF_DAYS)) > 0) {
      offerService.addOffer(offer) map {
        case None => InternalServerError(CreateOfferFailureResponse(offer = offer).asJson)
        case Some(retrievedOfferWithId) => Created(CreateOfferResponse(offerWithId = retrievedOfferWithId).asJson)
      }
    }
    else Future.successful(BadRequest(CreateOfferClientErrorResponse(offer = offer).asJson))
  }

  def get(offerId: UUID) = Action.async { implicit  request =>
    offerService.getOffer(OfferId(offerId)) map {
      case None => NotFound(OfferNotFoundResponse(offerId = OfferId(offerId)).asJson)
      case Some(offerWithId) => {
        if (offerWithId.offer.expireDate.compareTo(Instant.now) < 0) NotFound(OfferNotFoundResponse(offerId = OfferId(offerId)).asJson)
        else Ok(FoundOfferResponse(offerWithId = offerWithId).asJson)
      }
    }
  }

  def remove(offerId: UUID) = Action.async { implicit  request =>
    offerService.removeOffer(OfferId(offerId)) map {
      case None => NotFound(OfferNotFoundResponse(offerId = OfferId(offerId)).asJson)
      case Some(offerWithId) => Ok(RemoveOfferResponse(offerWithId = offerWithId).asJson)
    }
  }

}


