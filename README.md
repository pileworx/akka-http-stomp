akka-http-stomp
===============

Stomp specification support library for akka-http.

Licensed under the Apache 2 license.

[![Build Status](https://travis-ci.org/pileworx/akka-http-hal.svg?branch=master)](https://travis-ci.org/pileworx/akka-http-hal)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2b351c4ec64e441f8b1bbf6ea4db3492)](https://www.codacy.com/app/Pileworx/akka-http-hal?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pileworx/akka-http-hal&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/2b351c4ec64e441f8b1bbf6ea4db3492)](https://www.codacy.com/app/Pileworx/akka-http-hal?utm_source=github.com&utm_medium=referral&utm_content=pileworx/akka-http-hal&utm_campaign=Badge_Coverage)

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
