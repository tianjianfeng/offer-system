package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class OfferController @Inject()(val cc: ControllerComponents)(implicit exec: ExecutionContext)  extends AbstractController(cc) {

  def create = Action.async { implicit request =>
    Future.successful(Created)
  }

}


