package models

import models.Offers.Offer

object Responses {
  case class CreateOfferResponse(offer: Offer)
}