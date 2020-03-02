import com.google.inject.AbstractModule
import repo.{OfferRepo, OfferRepoImpl}
import services.{OfferService, OfferServiceImpl}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[OfferService]).to(classOf[OfferServiceImpl])
    bind(classOf[OfferRepo]).to(classOf[OfferRepoImpl])
  }

}