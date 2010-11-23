#!/bin/sed -f
/^# Attempt to set JAVA_HOME/ i\
export JAVA_HOME=/usr/lib/jvm/default-java\
export GRAILS_HOME=/usr/share/gradle\
