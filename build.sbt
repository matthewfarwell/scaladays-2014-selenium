organization  := "farwell.co.uk"

name := "scala-days-selenium"

version       := "1.0"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val akkaV = "2.1.4"
  val sprayV = "1.1.0"
  val seleniumV = "2.41.0"
  val scalatestV = "2.1.7"
  Seq(
    "io.spray"            %   "spray-httpx"   % sprayV,
    "io.spray"            %   "spray-client"     % sprayV,
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-testkit" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.2.5",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV,
    "org.specs2"          %%  "specs2"        % "2.2.3" % "test",
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "org.slf4j" % "jcl-over-slf4j" % "1.6.4",
    "org.slf4j" % "log4j-over-slf4j" % "1.6.4",
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "ch.qos.logback" % "logback-core" % "1.0.13",
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "com.typesafe" % "config" % "1.2.0",
    "org.scalatest" %% "scalatest" % scalatestV % "test",
    "org.seleniumhq.selenium" % "selenium-java" % seleniumV % "test",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.2" % "test",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2" % "test",
    "junit" % "junit" % "4.11" % "test",
    "org.scala-lang" % "scala-compiler" % "2.10.3" % "provided",
    "org.scala-lang" % "scala-reflect" % "2.10.3"  )
}

fork := true

javaOptions += "-Dwebdriver.chrome.driver=drivers/chromedriver.exe"

EclipseKeys.withSource := true

seq(Revolver.settings: _*)

//growl.GrowlingTests.growlSettings
