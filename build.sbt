import ReleaseTransformations._
import sbtrelease.Version

name := """spark-data-processing"""

scalaVersion := "2.10.3"

val sparkVersion = "1.6.0-cdh5.7.0"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.10" % sparkVersion,
  "org.apache.spark" % "spark-streaming-kafka_2.10" % sparkVersion,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc4",
  "mysql" % "mysql-connector-java" % "5.1.12",
  "net.emergingthreats" % "et-common_2.10" % "latest.snapshot" changing
)

dependencyOverrides ++= Set(
  "org.apache.kafka" % "kafka_2.10" % "0.9.0.1"
)

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
