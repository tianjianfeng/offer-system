package models

import models.Offers.{Offer, OfferWithId}

object Responses {
  case class CreateOfferResponse(offerWithId: OfferWithId)
}