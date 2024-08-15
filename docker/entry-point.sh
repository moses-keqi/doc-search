#!/bin/bash
echo "java ${JVM_OPTIONS} -jar ./app.jar"
nginx
java ${JVM_OPTIONS} -jar ./app.jar