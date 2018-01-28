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
  .settings(
    libraryDependencies += "com.thoughtworks.binding" %%% "dom" % "latest.release",
    scalaJSUseMainModuleInitializer := true
  )

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

lazy val frontendJvm =
  frontend.jvm
