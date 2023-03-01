#!/bin/sh

# Requires maven to be on the classpath
# Skips test phase

mvn clean install --batch-mode --no-transfer-progress -DskipTests=true -pl -d2fsam-web-embedded-server
#mvn clean install --batch-mode --no-transfer-progress -DskipTests=true -f dhis-web/pom.xml -U


