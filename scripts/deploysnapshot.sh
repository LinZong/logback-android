#!/usr/bin/env bash

user=${NEXUS_USERNAME?}
pass=${NEXUS_PASSWORD?}
./gradlew uploadArchives -x test -PNEXUS_USERNAME=${user} -PNEXUS_PASSWORD=${pass}
