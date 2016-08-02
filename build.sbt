import ReleaseTransformations._
import sbtrelease.Version

name := """spark-data-processing"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val hbaseVersion = "1.2.0-cdh5.7.0"

val sparkVersion = "1.6.0-cdh5.7.0"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % sparkVersion excludeAll(ExclusionRule("org.mortbay.jetty")),
  "org.apache.hbase" % "hbase-server" % hbaseVersion excludeAll(ExclusionRule("org.mortbay.jetty"), ExclusionRule("stax")),
  "org.apache.hbase" % "hbase-client" % hbaseVersion,
  "org.apache.hbase" % "hbase-common" % hbaseVersion,
  "org.apache.hbase" % "hbase-protocol" % hbaseVersion,
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc4",
  "mysql" % "mysql-connector-java" % "5.1.12",
  "net.emergingthreats" % "et-common_2.10" % "latest.snapshot" changing
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
