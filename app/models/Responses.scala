package models

import models.Offers.{Offer, OfferId, OfferWithId}

object Responses {

  sealed trait OfferResponse {
    def msg: String
  }
  case class CreateOfferResponse(msg: String = "An offer is created", offerWithId: OfferWithId) extends OfferResponse
  case class CreateOfferFailureResponse(msg: String = "Failed to create an offer", offer: Offer) extends OfferResponse
  case class FoundOfferResponse(msg: String = "An offer is found", offerWithId: OfferWithId) extends OfferResponse
  case class OfferNotFoundResponse(msg: String = "The offer could not be found", offerId: OfferId) extends OfferResponse
}