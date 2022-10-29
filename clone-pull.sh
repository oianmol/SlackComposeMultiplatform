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
cd gRPC-KMP || echo "can't cd"
git pull

cd ..

echo "<----Cloning slack_multiplatform_protos---->"
git clone https://github.com/oianmol/slack_multiplatform_protos
git pull

echo "<----Cloning slack_multiplatform_generate_protos---->"
git clone https://github.com/oianmol/slack_multiplatform_generate_protos || echo "already exists"
cd slack_multiplatform_generate_protos || echo "already exists"
git pull
git submodule update --init --recursive
git submodule update --recursive --remote

cd ..

echo "<----Cloning slack_multiplatform_domain---->"
git clone https://github.com/oianmol/slack_multiplatform_domain.git || echo "already exists"
cd slack_multiplatform_domain || echo "already exists"
git pull

cd ..

echo "<----Cloning slack_multiplatform_client_data_lib---->"
git clone https://github.com/oianmol/slack_multiplatform_client_data_lib  || echo "already exists"
cd slack_multiplatform_client_data_lib || echo "already exists"
git pull
git submodule update --init --recursive
git submodule update --recursive --remote

cd ..
echo "<----Cloning slack_multiplatform_grpc_server---->"
git clone https://github.com/oianmol/slack_multiplatform_grpc_server || echo "already exists"
cd slack_multiplatform_grpc_server || echo "already exists"
git pull

