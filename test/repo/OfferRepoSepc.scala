package repo

import java.time.Instant
import java.time.temporal.ChronoUnit

import models.Offers._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, OptionValues, WordSpec}

class OfferRepoSpec extends WordSpec with OptionValues with ScalaFutures with Matchers {

  "OfferRepo" should {
    "contains the newly added offer" in {

      val newOffer = Offer("lmn", Product("xyz"), Instant.now().plus(5, ChronoUnit.DAYS))
      val offerRepo = new OfferRepoImpl()
      val Some(addedOffer) = offerRepo.addOffer(newOffer).futureValue
      addedOffer.offer shouldBe newOffer
    }
  }
}
