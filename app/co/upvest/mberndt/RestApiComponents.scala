package co.upvest.mberndt

import play.api.BuiltInComponents
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.routing.Router
import play.api.routing.sird._

trait RestApiComponents
  extends BuiltInComponents
    with MessageBusComponents {
  def controllerComponents: ControllerComponents

  private val controller = new AbstractController(controllerComponents) {
    private def eventStream(f: PartialFunction[Message, String]) =
      Action {
        Ok.chunked(source.collect(f)).as(MimeTypes.EVENT_STREAM)
      }

    private def greetings(f: Int => Boolean) =
      eventStream {
        case Greeting(n) if f(n) =>
          s"""event: greeting
             |data: $n
             |
             |""".stripMargin
      }

    def evenGreetings: Action[AnyContent] = greetings(_ % 2 == 0)
    def oddGreetings: Action[AnyContent] = greetings(_ % 2 == 1)
    def coordinates: Action[AnyContent] = eventStream {
      case Coordinates(lat, long, description) =>
        s"""event: coordinates
           |data: ${Json.obj("lat" -> lat, "long" -> long, "description" -> description)}
           |
           |""".stripMargin
    }
  }

  override def router: Router = Router.from {
    case GET(p"/greets/even") =>
      controller.evenGreetings
    case GET(p"/greets/odd") =>
      controller.oddGreetings
    case GET(p"/coordinates") =>
      controller.coordinates
  }
  // Ideally we'd delegate to super.router if a request can't be routed.
  // I proposed and implemented this feature, but it won't be released
  // until Play 2.7.
  // https://github.com/playframework/playframework/pull/8012

}
