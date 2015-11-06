::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::      Dev environment startup script for Alfresco Community     ::
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
@echo off

set springloadedfile=%HOME%\.m2\repository\org\springframework\springloaded\1.2.3.RELEASE\springloaded-1.2.3.RELEASE.jar

if not exist %springloadedfile% (
  mvn validate -Psetup
)

set MAVEN_OPTS=-javaagent:"%springloadedfile%" -noverify -Xms256m -Xmx1G

::cd runner && mvn clean  && cd .. && mvnDebug install -Penterprise -Prun -nsu
mvn install -Penterprise -Prun -nsu
:: mvn install -Penterprise -Prun