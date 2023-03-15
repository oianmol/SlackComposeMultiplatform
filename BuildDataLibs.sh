#!/bin/bash
echo "Script executed from: ${PWD}"

./gradlew :slack_domain_layer:publishToMavenLocal
./gradlew :slack_generate_protos:publishToMavenLocal
./gradlew :slack_data_layer:publishToMavenLocal
./gradlew :encryptionlib:publishToMavenLocal
