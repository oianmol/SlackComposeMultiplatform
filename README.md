# SlackComposeMultiplatform

### This is a jetpack compose Slack Client Clone written in Kotlin Multiplatform following clean architecture principles.

Depends on[ gRPC-KMP](https://github.com/Anmol92verma/gRPC-KMP) which is a fork for gRPC library by [TimOrtel/GRPC-Kotlin-Multiplatform](https://github.com/TimOrtel/GRPC-Kotlin-Multiplatform)! 


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
