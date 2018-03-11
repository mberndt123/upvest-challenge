enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "com.vmunier" %% "scalajs-scripts" % "1.1.1",
  "org.webjars" %% "webjars-play" % "2.6.3",
  "org.webjars.bower" % "leaflet" % "1.2.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"
)

dockerExposedPorts := Seq(9000, 9011)
Universal / javaOptions += "-J-Djava.net.preferIPv4Stack=true"
