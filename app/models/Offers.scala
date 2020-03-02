package models

import java.time.Instant
import java.util.UUID

object Offers {

  case class OfferId(value: UUID)
  case class Product(name: String)
  case class Offer(description: String, product: Product, expireDate: Instant, discount: Float)
  case class OfferWithId(offerId: OfferId, offer: Offer)

}


