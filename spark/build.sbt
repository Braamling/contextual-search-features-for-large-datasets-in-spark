// Your sbt build file. Guides on how to write one can be found at
// http://www.scala-sbt.org/0.13/docs/index.html

spName := "ClueWeb12-contextual-features"

version := "0.0.1"

licenses := Seq("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

scalaVersion := "2.11.7"

sparkVersion := "2.1.1"

licenses := Seq("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

sparkComponents ++= Seq("mllib", "sql")

spAppendScalaVersion := true

mainClass in (Compile, run) := Some("BuildFeatures")

spIncludeMaven := true

spIgnoreProvided := true

javaOptions ++= Seq("-Xmx2G", "-XX:MaxPermSize=256m")

val sparkVer = "2.1.1"

libraryDependencies ++= Seq(
  "org.jwat" % "jwat-gzip" % "1.0.0",
  "org.jwat" % "jwat-common" % "1.0.0",
  "org.jwat" % "jwat-warc" % "1.0.0",
//  "junit" % "junit" % "4.12",
  "commons-io" % "commons-io" % "1.3.2",
  ("org.apache.hadoop" % "hadoop-mapreduce-client-common" % "2.4.0" % "provided")
    .exclude("org.mortbay.jetty", "servlet-api"),
  "org.apache.pig" % "pig" % "0.12.1" % "provided",
  "org.jsoup" % "jsoup" % "1.11.2"
)

resolvers ++= Seq(
  "Atilika Open Source repository" at "http://www.atilika.org/nexus/content/repositories/atilika"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("org", "mortbay", xs @ _*) => MergeStrategy.last
  case PathList("junit", "junit", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("org", "jwat", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x => MergeStrategy.last
}
