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

JAVA_HOME=${JAVA_HOME:-"$(dirname $(which java))/.."}
JAVA=${JAVA:-"${JAVA_HOME}/bin/java"}

kill -9  $(jps | grep helm-starter | awk '{print $1}')



