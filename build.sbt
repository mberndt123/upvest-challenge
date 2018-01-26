lazy val backend =
  project.dependsOn(frontend)
  .settings(
    scalaJSProjects := Seq(frontend),
    Assets / pipelineStages := Seq(scalaJSPipeline)
  )
lazy val frontend =
  project
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)


