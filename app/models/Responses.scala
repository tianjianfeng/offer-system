package models

import models.Offers.{Offer, OfferWithId}

object Responses {

  sealed trait OfferResponse {
    def msg: String
  }
  case class CreateOfferResponse(msg: String = "An offer is created", offerWithId: OfferWithId) extends OfferResponse
  case class CreateOfferFailureResponse(msg: String = "Failed to create an offer", offer: Offer) extends OfferResponse
}