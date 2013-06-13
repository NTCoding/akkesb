akkesb
======

A platform-decoupled, allowing different programming languages to communicate, enterprise service bus with the design goals of being reliable, fault-tolerant and robust.

To learn more about this project, please look in the "doc" folder in the root of the project. It contains design diagrams drawn in visio alongside the project's vision and roadmap.

If you would like to join this project then please get in touch. You don't need to know Scala or Akka - just an enthusiasm to learn.

You can see the CI build on Travis here: https://travis-ci.org/NTCoding/akkesb

[![Build Status](https://travis-ci.org/NTCoding/akkesb.png?branch=master)](https://travis-ci.org/NTCoding/akkesb)


Dependencies
------------

1. DBus - e.g for Debian/Ubuntu/Crashbang: 

    apt-get install dbus-java-bin for

2. unix-java - you need to tell Java where to find libunix-java.so. E.g do this in .zshrc: 

    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/jni"

