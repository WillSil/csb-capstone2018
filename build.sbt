name := "automate-csb"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean, SbtWeb)

scalaVersion := "2.11.8"

libraryDependencies += javaJdbc
libraryDependencies += ehcache
libraryDependencies += guice
libraryDependencies += javaWs
libraryDependencies += filters
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.0"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212.jre7"
libraryDependencies += "com.h2database" % "h2" % "1.4.196" % Test
libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "1.0.0"
libraryDependencies += "com.google.firebase" % "firebase-admin" % "5.4.0"
libraryDependencies += "net.spy" % "spymemcached" % "2.12.1"
libraryDependencies += "io.swagger" %% "swagger-play2" % "1.6.1-SNAPSHOT"
libraryDependencies += "org.webjars" % "swagger-ui" % "2.2.0"
libraryDependencies += "io.jsonwebtoken" % "jjwt" % "0.7.0"
libraryDependencies += "io.ebean" % "ebean-querybean" % "10.3.1"
libraryDependencies += "io.ebean" % "ebean-agent" % "10.3.1"
libraryDependencies += "io.ebean" % "querybean-generator" % "10.2.1"
libraryDependencies += "io.ebean.tools" % "finder-generator" % "4.2.1"
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.23.0" exclude("com.google", "common.collect")
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0" exclude ("com.google", "common.collect")
libraryDependencies += "com.google.apis" % "google-api-services-gmail" % "v1-rev73-1.21.0" exclude ("com.google", "common.collect")
libraryDependencies += "com.google.apis" % "google-api-services-oauth2" % "v2-rev131-1.21.0" exclude("com.google", "common.collect")
libraryDependencies += "com.google.guava" % "guava" % "23.0"

playEbeanAgentArgs += ("transientInternalFields" -> "true")

updateOptions := updateOptions.value.withLatestSnapshots(false)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += Resolver.mavenLocal

playEbeanDebugLevel := 9

javaOptions in Test ++= Seq(
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9998",
  "-Xms512M",
  "-Xmx1536M",
  "-Xss1M",
  "-XX:MaxPermSize=384M"
)

testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

inConfig(Test)(PlayEbean.scopedSettings)

playEbeanModels in Test := Seq("models.ebean.*", "models.ebean.business.*", "models.ebean.file.*",
  "models.ebean.invite.*", "models.ebean.status.*", "models.ebean.sync.*", "models.ebean.user.*")

val copyQueryBeans = TaskKey[Unit]("copyQueryBeans", "Copies the query beans to the routes generated directory")

