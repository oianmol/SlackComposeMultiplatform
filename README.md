# SlackComposeMultiplatform (KMP with gRPC & In-Development)


![Component 1](https://user-images.githubusercontent.com/4393101/197988428-87a04d5e-94e0-4f7e-9c34-48d04983b081.png)


## 🏗️️ Built with 💪🏼 using Jetpack Compose And Kotlin Multiplatform 😁

| What                                    | How                                                                                                                                                                             |
|-----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 🎭 User Interface (Android,Desktop,iOS) | [Jetpack Compose JB!](https://developer.android.com/jetpack/compose)                                                                                                            |
| 🏗 Architecture                         | [Decompose + Clean Architecture](https://arkivanov.github.io/Decompose/)                                                                                                        |
| 💉 DI (Android)                         | [Koin](https://insert-koin.io/)                                                                                                                                                 |
| 🌊 Async                                | [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) |
| 🌐 Networking                           | [gRPC](https://grpc.io/)                                                                                                                                                        |
| 📄 Pagination                           | [moko-paging](https://github.com/icerockdev/moko-paging)                                                                                                                        |
| 🪔 Lint                                 | [ktlint gradle plugin](https://github.com/JLLeitschuh/ktlint-gradle) , [ktlint Pinterest](https://github.com/pinterest/ktlint)                                                  |
| 🤿 Testing with Turbine                                 | [Turbine](https://github.com/cashapp/turbine)                                                 |


## Prerequisities

1. Fetch [grpc-KMP](https://github.com/oianmol/gRPC-KMP) submodule and install :plugin and :grpc-multiplatform-lib to mavenLocal()
2. Setup env variables for the grpc-server with firebase creds and email info using setup.sh in the root of this project 
3. Run the server 

The project supports android, compose iOS and jvm platform, make sure you match the ip:addr in the class GrpcCalls() once you run the slackserver locally!

## Architecture

![Slack Multiplatform (3)](https://user-images.githubusercontent.com/4393101/205478274-23b55650-5676-4530-bd12-cdbd96dd098d.png)


### Video Demo with Andorid, iOS and JVM Desktop

https://user-images.githubusercontent.com/4393101/229371347-1cebf7bf-384d-4ec0-bfcf-19b1e34e6290.mp4

### UI Tests introduced with commoncomposeui module

https://github.com/oianmol/SlackComposeMultiplatform/assets/4393101/05a37ed9-c27c-41dd-b493-26cb55506095


License
=======
    Copyright 2023 Anmol Verma

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
