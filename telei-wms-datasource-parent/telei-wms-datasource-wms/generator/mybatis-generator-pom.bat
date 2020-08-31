@echo off
git pull origin master
call mvn -e -U -DskipTests mybatis-generator:generate -f mybatis-generator-pom.xml -X
cd ../src
svn add . --no-ignore --force
svn ci -m "commit by mybatis generator"