#!/usr/bin/env bash
svn up
mvn -e -U -DskipTests com.nuochen.framework:nuochen-framework-autocoding-mavenplugin:generate -f mybatis-generator-pom.xml -X
mvn  -DskipTests com.nuochen.framework:nuochen-framework-autocoding-mavenplugin:generate -f mybatis-generator-pom.xml
cd ../src
svn add . --no-ignore --force
#svn ci -m "commit by mybatis generator"
