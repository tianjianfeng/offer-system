package services

import javax.inject.Inject
import models.Offers._
import repo.OfferRepo

import scala.concurrent.Future

trait OfferService {

  def addOffer(offer: Offer): Future[Option[OfferWithId]]
  def getOffer(offerId: OfferId): Future[Option[OfferWithId]]
  def removeOffer(offerId: OfferId): Future[Option[OfferWithId]]
}

class OfferServiceImpl @Inject()(offerRepo: OfferRepo) extends OfferService {

  def addOffer(offer: Offer): Future[Option[OfferWithId]] = offerRepo.addOffer(offer)

  def getOffer(offerId: OfferId): Future[Option[OfferWithId]] = offerRepo.getOffer(offerId)

  def removeOffer(offerId: OfferId): Future[Option[OfferWithId]] = offerRepo.removeOffer(offerId)

}