# Transcoding gRPC to HTTP/JSON

Sample project showing how to expose a gRPC service as a HTTP/JSON api. 

[grpc-as-an-alternative-to-rest](https://blog.jdriven.com/2018/10/grpc-as-an-alternative-to-rest/)


Built with Java 11, but 1.8 should also be suppored, change `sourceCompatiblity` in the build.gradle to 1.8


## Getting started

1. Open the project in your favourite IDE 
     * run `./gradlew idea` or `./gradlew eclipse` to configure your IDE to detect the generated code directories.
     
1. Run `./gradlew build`   on Linux/Mac,  or `gradlew.bat build` on Windows to run a build.
1. Run `./gradlew generateProto` generates sources from your .proto files  

## Transcoding gRPC to HTTP/JSON using Envoy proxy

Generating the protobuf descriptor that envoy requires to expose the service as 
a http/json api. 

1. Goto: https://github.com/protocolbuffers/protobuf/releases/latest" +
2. download choose the precompiled version " +

       for linux:   protoc-3.6.1-linux-x86_64.zip" +
       for windows: protoc-3.6.1-win32.zip" +
       for mac:     protoc-3.6.1-osx-x86_64.zip" +

3. extract it somewhere in your PATH
4. Run the protoc command from within this project's root directory!
   notice that build/extracted-include-protos/main contains proto files from
   jar files on the classpath, for example: `com.google.api.grpc:googleapis-common-protos:0.0.3`
   this dependency contains the sources for `google.api.http` options we use
   in the .proto file
   
       protoc -I. -Ibuild/extracted-include-protos/main --include_imports --include_source_info --descriptor_set_out=reservation_service_definition.pb src/main/proto/reservation_service.proto
       
5. Run envoy (docker container) from within this directory

       docker run -it --rm --name envoy --network="host" -v "$(pwd)/reservation_service_definition.pb:/tmp/reservation_service_definition.pb:ro" -v "$(pwd)/envoy-config.yml:/etc/envoy/envoy.yaml:ro" envoyproxy/envoy   
 
6. Create a reservation

       curl -X POST \
         http://localhost:51051/v1/reservations/ \
         -H 'content-type: application/json' \
         -d '{
           "title": "Test",
           "venue": "Rotterdam",
           "timestamp": "2018-10-11T15:15:15",
           "attendees": 15
       }'
        
7. Retrieve it (substitute ID): 

       curl -X GET http://localhost:51051/v1/reservations/<enter-id!!>
          
8. Delete it: 

       curl -X DELETE http://localhost:51051/v1/reservations/<enter-id!!>