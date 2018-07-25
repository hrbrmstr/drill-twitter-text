.PHONY: udf deps install restart

DRILL_HOME ?= /usr/local/drill

udf:
	mvn --quiet clean package -DSkipTests

install:
	cp target/drill-twitter-text*.jar ${DRILL_HOME}/jars/3rdparty && \
	cp deps/twitter-text-2.0.10.jar ${DRILL_HOME}/jars/3rdparty

restart:
	drillbit.sh restart

deps:
	mvn dependency:copy-dependencies -DoutputDirectory=deps
