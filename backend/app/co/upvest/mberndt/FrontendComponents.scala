package co.upvest.mberndt

import controllers.AssetsComponents
import org.webjars.play.{RequireJS, WebJarComponents, WebJarsUtil}
import play.api.BuiltInComponents
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.routing.Router
import play.api.routing.sird._

trait FrontendComponents extends BuiltInComponents with AssetsComponents with WebJarComponents {
  def controllerComponents: ControllerComponents

  def webJarsUtil: WebJarsUtil

  private lazy val frontendTemplate = new views.html.co.upvest.mberndt.frontend(webJarsUtil, assetsPrefix)

  private val frontendCtrl = new AbstractController(controllerComponents) {
    def frontend = Action {
      Ok(frontendTemplate())
    }
  }

  private lazy val webjarsRouter =
    new webjars.Routes(httpErrorHandler, new RequireJS(webJarsUtil), webJarAssets).withPrefix("/lib")

  private lazy val assetsPrefix = "assets"
  private lazy val AssetsPattern = PathExtractor.cached(Seq(s"/$assetsPrefix/", "*"))

  abstract override def router: Router = Router.from {
    super.router.routes.orElse(webjarsRouter.routes).orElse {
      case p"/frontend" =>
        frontendCtrl.frontend
      case AssetsPattern(file) =>
        assets.at(file)
    }
  }
}
