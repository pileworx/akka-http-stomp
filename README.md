akka-http-stomp
===============

Stomp specification support library for akka-http.

Licensed under the Apache 2 license.

[![Build Status](https://travis-ci.org/pileworx/akka-http-stomp.svg?branch=develop)](https://travis-ci.org/pileworx/akka-http-stomp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c0ad753aa3974c4a9f38afec61d67fb3)](https://www.codacy.com/app/Pileworx/akka-http-stomp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pileworx/akka-http-stomp&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/c0ad753aa3974c4a9f38afec61d67fb3)](https://www.codacy.com/app/Pileworx/akka-http-stomp?utm_source=github.com&utm_medium=referral&utm_content=pileworx/akka-http-stomp&utm_campaign=Badge_Coverage)

Getting Started
---------------
We are currently working to add the binary to Maven Central. Until then, add using sbt.

In your build.sbt add:
```scala
lazy val akkaHttpStomp = ProjectRef(
  uri("https://github.com/pileworx/akka-http-stomp.git"),
  "akka-http-stomp")

lazy val root = (project in file(".")).dependsOn(akkaHttpStomp)
```

TODO
-----------
Publish to maven central.  
Find more contributors (hint).
