package co.upvest.mberndt

import controllers.AssetsComponents
import play.api.BuiltInComponents
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.routing.Router
import play.api.routing.sird._

trait FrontendComponents extends BuiltInComponents with AssetsComponents {
  def controllerComponents: ControllerComponents

  private val frontendCtrl = new AbstractController(controllerComponents) {
    def frontend = Action {
      Ok(views.html.co.upvest.mberndt.frontend())
    }
  }

  abstract override def router: Router = Router.from {
    super.router.routes.orElse {
      case p"/frontend" =>
        frontendCtrl.frontend
      case p"/assets/$file*" =>
        assets.at(file)
    }
  }
}
