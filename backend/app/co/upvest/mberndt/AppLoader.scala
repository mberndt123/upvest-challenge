package co.upvest.mberndt

import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents

class AppLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
    new MyComponents(context).application
  }
}

class MyComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with DefaultMessageBusComponents
    with RestApiComponents
    with TcpListenComponents {
}