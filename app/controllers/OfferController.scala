package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import javax.inject.Inject
import models.Offers.Offer
import models.Responses.{CreateOfferFailureResponse, CreateOfferResponse}
import play.api.libs.circe.Circe
import play.api.mvc._
import services.OfferService

import scala.concurrent.ExecutionContext

class OfferController @Inject()(val cc: ControllerComponents, offerService: OfferService)(implicit exec: ExecutionContext)  extends AbstractController(cc) with Circe {

  def create = Action.async(circe.json[Offer]) { implicit request =>
    val offer = request.body

    offerService.addOffer(offer) map {
      case None => InternalServerError(CreateOfferFailureResponse(offer = offer).asJson)
      case Some(retrievedOfferWithId) => Created(CreateOfferResponse(offerWithId = retrievedOfferWithId).asJson)
    }
  }

}


