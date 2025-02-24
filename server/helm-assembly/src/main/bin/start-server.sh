#!/usr/bin/env bash

this="${BASH_SOURCE-$0}"
common_bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
script="$(basename -- "$this")"
this="$common_bin/$script"

# convert relative path to absolute path
config_bin=$(dirname "$this")
script=$(basename "$this")
config_bin=$(cd "$config_bin"; pwd)
this="$config_bin/$script"


HELM_HOME=$(dirname $(dirname "${this}"))
export HELM_HOME
HELM_CONF_DIR="${HELM_CONF_DIR:-${HELM_HOME}/conf}"
HELM_LOGS_DIR="${HELM_LOGS_DIR:-${HELM_HOME}/logs}"
HELM_LIB_DIR="${HELM_LIB_DIR:-${HELM_HOME}/lib}"


JAVA_HOME=${JAVA_HOME:-"$(dirname $(which java))/.."}
JAVA=${JAVA:-"${JAVA_HOME}/bin/java"}

JVM_OPTS="-Xmx1024m"
DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:9909"
SPRING_ARGS="--spring.config.location=${HELM_CONF_DIR}/application.properties --logging.config=${HELM_CONF_DIR}/logback-spring.xml"


CMD="java  $DEBUG_OPTS $JVM_OPTS  -Dlogging.file.path=${HELM_LOGS_DIR} -Duser.timezone=UTC+8 -Dfile.encoding=UTF-8 -jar
${HELM_LIB_DIR}/helm-starter-*.jar $SPRING_ARGS"
nohup  $CMD > /dev/null 2>&1  &