addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.1.0-RC1")
// platform independent Debian packaging
libraryDependencies += "org.vafer" % "jdeb" % "1.3" artifacts (Artifact("jdeb", "jar", "jar"))

// fast turnaround / restart app
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.6")
