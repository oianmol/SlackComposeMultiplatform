# SlackComposeMultiplatform

### This is a jetpack compose Slack Client Clone written in Kotlin Multiplatform following clean architecture principles.

Depends on [gRPC-KMP](https://github.com/Anmol92verma/gRPC-KMP) which is a fork for gRPC library by [TimOrtel/GRPC-Kotlin-Multiplatform](https://github.com/TimOrtel/GRPC-Kotlin-Multiplatform)! 

Important Points

The project has submodules now
1. [Protos](https://github.com/Anmol92verma/slack_multiplatform_protos)
2. [GenerateProtos](https://github.com/Anmol92verma/slack_multiplatform_generate_protos)
3. [SlackClientLibrary](https://github.com/Anmol92verma/slack_multiplatform_client_data_lib)
4. [This project](https://github.com/Anmol92verma/slackcomposemultiplatform)
5. [Backend Server in Kotlin](https://github.com/Anmol92verma/slack_multiplatform_grpc_server)

1. Install gRPC library on mavenLocal() once you clone [gRPC-KMP](https://github.com/Anmol92verma/gRPC-KMP)
2. Build and install to mavenLocal grpc-multiplatform-lib & plugin module (Note: Use Xcode 13.0 only! 14.0.* doesnt work with grpc!)
3. Import SlackServer in Intellij Idea and run the server.
4. iOS build fails with ref issue. https://github.com/TimOrtel/GRPC-Kotlin-Multiplatform/issues/11

I haven't tried running iOS build for some time it depends a lot on the grpc-kmp library support for iOS
.
Once the gradle sync is successful you need to run the task which deploys the app on simulator. 
There's a specific task to deploy on iPad and iPhones.

`./gradlew iosDeployIPhone8Debug`

Also make sure you use the grpc kmp library from the fork that i created as mentioned above

The android and jvm run's fine, make sure you match the ip address in GrpcCalls class of your system once you run the slackserver module locally!

## Architecture

![Architecture](https://user-images.githubusercontent.com/4393101/194482641-9a52c4c8-e609-4fde-9b15-5d44578269b3.png)


## Latest Video Demos!


https://user-images.githubusercontent.com/4393101/194718377-7495cb3f-a104-46e3-8ab7-481ec2397e33.mp4


https://user-images.githubusercontent.com/4393101/194718380-d65a3869-4c4a-4409-9ac2-c952dc219b6e.mp4






```
SlackComposeMultiplatformProject
│    
│      
│
└───platform (Android/Desktop/iOS) = Compose multiplatform Desktop
│      
│     
└───generate-proto = Generates the java/kotlin files using protobuf and grpc libs
│  
│     
│   
└───protos = Contains the proto files
│     
│       
│   
└───Slack Server =  the backend server which has the logic for the client app!
```

Video Demo for JVM Desktop with Responsive UI

https://user-images.githubusercontent.com/4393101/188278261-4553ea2b-e80f-4515-be85-e2eba646930b.mp4

Video Demo for Jetpack Compose for iOS

https://user-images.githubusercontent.com/32521663/189109199-6743606c-0e28-4d10-b7ba-61ec3641ed55.mp4


## 🏗️️ Built with ❤️ using Jetpack Compose 😁

| What            | How                        |
|----------------	|------------------------------	|
| 🎭 User Interface (Android,Desktop,iOS)   | [Jetpack Compose](https://developer.android.com/jetpack/compose)                |
| 🏗 Architecture    | [Clean](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)                            |
| 💉 DI (Android)                | [Koin](https://insert-koin.io/)                        |
| 🌊 Async            | [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)                |
| 🌐 Networking        | [Ktor.IO](https://ktor.io/)                        |
| 📄 Parsing            | [KotlinX](https://kotlinlang.org/docs/serialization.html)                            |


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
