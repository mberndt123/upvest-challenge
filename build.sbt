lazy val backend =
  project.dependsOn(frontendJvm)
  .settings(
    scalaJSProjects := Seq(frontendJs),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    WebKeys.webModuleGenerators in Assets -= (WebKeys.webJars in Assets).taskValue
  )
lazy val frontend =
  crossProject

lazy val frontendJs =
  frontend.js
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(
    libraryDependencies ++= Seq(
      "com.thoughtworks.binding" %%% "dom" % "latest.release",
      "com.cibo" %%% "leaflet-facade" % "1.0.3"
    ),
    resolvers += Resolver.bintrayRepo("cibotech", "public"),
    scalaJSUseMainModuleInitializer := true,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
    // scalacOptions += "-Ymacro-debug-lite"
  )

lazy val frontendJvm =
  frontend.jvm
  .settings(
    libraryDependencies += "com.lihaoyi" %%% "fastparse" % "1.0.0"
  )