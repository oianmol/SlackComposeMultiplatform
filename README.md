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


Video Demo for JVM Desktop and Android

https://user-images.githubusercontent.com/4393101/201595781-c8669824-d774-4c82-b1e2-4814913357a8.mp4


Video Demo for Jetpack Compose for iOS

https://user-images.githubusercontent.com/32521663/189109199-6743606c-0e28-4d10-b7ba-61ec3641ed55.mp4



MIT License

Copyright (c) 2022 Anmol Verma

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
