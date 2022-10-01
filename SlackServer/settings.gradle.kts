include(":generate-proto")
include(":protos")

project(":generate-proto").projectDir = file("../generate-proto")
project(":protos").projectDir = file("../protos")
rootProject.name = "SlackServer"

