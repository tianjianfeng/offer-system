package models

import java.util.UUID

object Offers {

  case class OfferId(offerId: UUID)
  case class Offer(description: String)
  case class OfferWithId(offerId: OfferId, offer: Offer)

}


