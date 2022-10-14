echo "<----Building Slack Multiplatform---->"

read -p "Please enter the path where you would like to clone: " -r r1
parent=$(dirname "$r1")
cd "$parent"

echo "<----Cloning gRPC-KMP---->"
git clone https://github.com/oianmol/gRPC-KMP
cd gRPC-KMP
./gradlew grpc-multiplatform-lib:build && ./gradlew grpc-multiplatform-lib:publishToMavenLocal
./gradlew plugin:build && ./gradlew plugin:publishToMavenLocal

echo "<----Cloning slack_multiplatform_protos---->"
git clone https://github.com/oianmol/slack_multiplatform_protos

echo "<----Cloning slack_multiplatform_generate_protos---->"
git clone https://github.com/oianmol/slack_multiplatform_generate_protos
cd slack_multiplatform_generate_protos
git submodule update --init --recursive
echo "<----Building slack_multiplatform_protos---->"
./gradlew build && ./gradlew publishToMavenLocal

echo "<----Building slack_multiplatform_client_data_lib---->"
cd .. && git clone https://github.com/oianmol/slack_multiplatform_client_data_lib && cd slack_multiplatform_client_data_lib
./gradlew build && ./gradlew publishToMavenLocal

echo "<----Running slack_multiplatform_grpc_server---->"
cd .. && git clone https://github.com/oianmol/slack_multiplatform_grpc_server && cd slack_multiplatform_grpc_server
./gradlew run

#echo "<----Running SlackComposeMultiplatform Client---->"
#git clone https://github.com/oianmol/SlackComposeMultiplatform && cd SlackComposeMultiplatform
#./gradlew run


