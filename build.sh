#!/bin/bash
echo "<----Building Slack Multiplatform---->"
echo "Script executed from: ${PWD}"
BASEDIR=$(dirname $PWD)

read -p "Do you want to proceed in $BASEDIR? (yes/no) " yn

case $yn in
	yes ) echo ok, we will proceed;;
	no ) echo exiting...;
		exit;;
	* ) echo invalid response;
		exit 1;;
esac



echo doing stuff...
cd ..

echo "<----Cloning gRPC-KMP---->"
git clone https://github.com/oianmol/gRPC-KMP
cd gRPC-KMP
./gradlew grpc-multiplatform-lib:build && ./gradlew grpc-multiplatform-lib:publishToMavenLocal
./gradlew plugin:build && ./gradlew plugin:publishToMavenLocal

cd ..

echo "<----Cloning slack_multiplatform_protos---->"
git clone https://github.com/oianmol/slack_multiplatform_protos

echo "<----Cloning slack_multiplatform_generate_protos---->"
git clone https://github.com/oianmol/slack_multiplatform_generate_protos
cd slack_multiplatform_generate_protos
git submodule update --init --recursive
echo "<----Building slack_multiplatform_protos---->"
./gradlew build && ./gradlew publishToMavenLocal

cd ..
echo "<----Building slack_multiplatform_client_data_lib---->"
git clone https://github.com/oianmol/slack_multiplatform_client_data_lib && cd slack_multiplatform_client_data_lib
./gradlew build && ./gradlew publishToMavenLocal

cd ..
echo "<----Running slack_multiplatform_grpc_server---->"
git clone https://github.com/oianmol/slack_multiplatform_grpc_server && cd slack_multiplatform_grpc_server
./gradlew run

#echo "<----Running SlackComposeMultiplatform Client---->"
#git clone https://github.com/oianmol/SlackComposeMultiplatform && cd SlackComposeMultiplatform
#./gradlew run


