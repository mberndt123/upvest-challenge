lazy val backend =
  project.dependsOn(frontendJvm)
  .settings(
    scalaJSProjects := Seq(frontendJs),
    Assets / pipelineStages := Seq(scalaJSPipeline)
  )
lazy val frontend =
  crossProject
  .settings(libraryDependencies += "com.lihaoyi" %%% "fastparse" % "1.0.0")

lazy val frontendJs =
  frontend.js
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)

lazy val frontendJvm =
  frontend.jvm
