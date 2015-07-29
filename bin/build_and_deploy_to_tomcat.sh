#!/bin/bash -x

theRunPath=`dirname "${0}"`
pushd "${theRunPath}/.."

theBasePath=`pwd`
theBaseName=${theBasePath##*/}

# Build
mvn install -DskipTests

cp -fv \
	./target/*.war \
	/e/tools/apache-tomcat/webapps

popd

