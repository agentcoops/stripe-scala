name := "stripe-scala"

version := io.Source.fromFile("VERSION").mkString.trim

organization := "com.stripe"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test")

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Resources" at "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases")

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <url>http://www.stripe.com/</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:stripe/stripe-scala.git</url>
    <connection>scm:git:git@github.com:stripe/stripe-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>siddarth</id>
      <name>Siddarth Chandrasekaran</name>
      <url>https://stripe.com/about#siddarth</url>
    </developer>
  </developers>
)

