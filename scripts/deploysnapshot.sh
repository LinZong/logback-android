#!/usr/bin/env bash
. gradle.properties

[[ "$TRAVIS" == true ]] && settings='--settings config/travisMavenSettings.xml' || settings=''

mvn versions:set -DnewVersion=${VERSION_NAME}

mvn clean deploy $settings              \
    -Pdebug                             \
    -B                                  \
    -DskipTests=true                    \
    -Dslf4jVersion=$slf4jVersion

mvn versions:revert
