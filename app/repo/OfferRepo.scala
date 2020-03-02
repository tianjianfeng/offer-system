package repo

import java.util.UUID

import javax.inject.Inject
import models.Offers._

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future


trait OfferRepo {

  def addOffer(offer: Offer): Future[Option[OfferWithId]]
  def getOffer(offerId: OfferId): Future[Option[OfferWithId]]


}

class OfferRepoImpl @Inject() (offers: TrieMap[OfferId, Offer] =  TrieMap[OfferId, Offer]()) extends OfferRepo {

  def addOffer(offer: Offer): Future[Option[OfferWithId]] = {
    val offerId = OfferId(UUID.randomUUID())
    offers += ((offerId, offer))
    Future.successful(offers.get(offerId).map(o => OfferWithId(offerId, offer)))
  }

  def getOffer(offerId: OfferId): Future[Option[OfferWithId]] = Future.successful(offers.get(offerId) map (OfferWithId(offerId, _)))

}