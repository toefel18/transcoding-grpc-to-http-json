# Transcoding gRPC to HTTP/JSON

Sample project showing how to expose a gRPC service as a HTTP/JSON api. 

[grpc-as-an-alternative-to-rest](https://blog.jdriven.com/2018/10/grpc-as-an-alternative-to-rest/)


Built with Java 11, but 1.8 should also be suppored, change `sourceCompatiblity` in the build.gradle to 1.8


## Getting started

1. Open the project in your favourite IDE 
     * run `./gradlew idea` or `./gradlew eclipse` to configure your IDE to detect the generated code directories.
     
1. Run `./gradlew`   on Linux/Mac,  or `gradlew.bat` on Windows to run a build.
1. Run `./gradlew generateProto` to regenerate sources from your .proto files

