import ReleaseTransformations._
import sbtrelease.Version
import AssemblyKeys._
import sbt.Package.ManifestAttributes

name := """spark-data-processing"""

scalaVersion := "2.10.3"

val sparkVersion = "1.6.0-cdh5.7.0"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "joda-time" % "joda-time" % "2.3",
  "org.apache.spark" % "spark-streaming_2.10" % sparkVersion excludeAll(ExclusionRule("org.mortbay.jetty")),
  "org.apache.spark" % "spark-streaming-kafka_2.10" % sparkVersion excludeAll(
														ExclusionRule("org.mortbay.jetty")),
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.4-1205-jdbc41",
  "mysql" % "mysql-connector-java" % "5.1.12",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "net.emergingthreats" % "et-common_2.10" % "latest.snapshot" changing,
  "net.emergingthreats" % "et-common-play_2.10" % "latest.snapshot" changing
)

dependencyOverrides ++= Set(
  "org.apache.kafka" % "kafka_2.10" % "0.9.0.1"
)

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case m if m.toLowerCase.matches("meta-inf/.*\\.sf$") => MergeStrategy.discard
  case PathList("META-INF", "MANIFEST.MF", xs @ _*) => MergeStrategy.discard
  case PathList(ps @ _*) => MergeStrategy.first
  case x => old(x)
}
}

testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "console")

resolvers ++= Seq(
  "Cloudera" at "https://repository.cloudera.com/content/repositories/releases",
  "ET Common" at "http://10.55.56.163:8081/nexus/content/repositories/et-common"
)

unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"

releaseProcess := Seq[ReleaseStep](
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

releaseVersionBump := sbtrelease.Version.Bump.Next

releaseVersion := {ver => Version(ver).map(_.withoutQualifier.string).getOrElse(ver)}

releaseNextVersion := {ver => Version(ver).map(_.bump(sbtrelease.Version.Bump.Next).withoutQualifier.string).getOrElse(ver)}

releaseCommitMessage:= s"release increment"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
