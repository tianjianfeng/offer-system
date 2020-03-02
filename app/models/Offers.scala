package models

import java.time.Instant
import java.util.UUID

object Offers {

  case class OfferId(offerId: UUID)
  case class Product(name: String)
  case class Offer(description: String, product: Product, expireDate: Instant)
  case class OfferWithId(offerId: OfferId, offer: Offer)

}


