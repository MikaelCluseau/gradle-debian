#!/bin/sed -f
/^# Determine the Java command to use to start the JVM/ i\
export JAVA_HOME=/usr/lib/jvm/default-java\

