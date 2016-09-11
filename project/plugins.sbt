addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.5.0")
addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.5.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

// Heroku wants to execute "sbt compile stage" which is provided by this package.
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.8.0")
