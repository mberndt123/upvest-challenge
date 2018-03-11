package co.upvest.mberndt

import org.webjars.play.WebJarComponents
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import _root_.controllers.AssetsComponents
import play.api._

class AppLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
    new MyComponents(context).application
  }
}

trait EmptyRouterComponents extends BuiltInComponents {
  override def router: Router = Router.empty
}

class MyComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with EmptyRouterComponents
    with HttpFiltersComponents
    with DefaultMessageBusComponents
    with RestApiComponents
    with TcpListenComponents
    with FrontendComponents
    with AssetsComponents
