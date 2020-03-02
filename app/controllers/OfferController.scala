package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.circe.Circe
import io.circe.generic.auto._
import io.circe.syntax._
import models.Offers.Offer
import models.Responses.CreateOfferResponse

import scala.concurrent.{ExecutionContext, Future}

class OfferController @Inject()(val cc: ControllerComponents)(implicit exec: ExecutionContext)  extends AbstractController(cc) with Circe {

  def create = Action.async(circe.json[Offer]) { implicit request =>
    val offer = request.body
    Future.successful(Created(CreateOfferResponse(offer).asJson))
  }

}


