package controllers

import java.util.UUID

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.circe.Circe
import io.circe.generic.auto._
import io.circe.syntax._
import models.Offers.{Offer, OfferId, OfferWithId}
import models.Responses.{CreateOfferFailureResponse, CreateOfferResponse}
import repo.OfferRepoImpl

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}

class OfferController @Inject()(val cc: ControllerComponents)(implicit exec: ExecutionContext)  extends AbstractController(cc) with Circe {

  val offerRepo = new OfferRepoImpl

  def create = Action.async(circe.json[Offer]) { implicit request =>
    val offer = request.body

    val offerId = OfferId(UUID.randomUUID())

    offerRepo.addOffer(offer) map {
      case None => InternalServerError(CreateOfferFailureResponse(offer = offer).asJson)
      case Some(retrievedOfferWithId) => Created(CreateOfferResponse(offerWithId = retrievedOfferWithId).asJson)
    }
  }

}


