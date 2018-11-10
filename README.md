# Transcoding gRPC to HTTP/JSON

Sample project showing how to expose a gRPC service as a HTTP/JSON api. 

[grpc-as-an-alternative-to-rest](https://blog.jdriven.com/2018/10/grpc-as-an-alternative-to-rest/)


Built with Java 11, but 1.8 should also be suppored, change `sourceCompatiblity` in the build.gradle to 1.8


## Getting started

1. Open the project in your favourite IDE 
     * run `./gradlew idea` or `./gradlew eclipse` to configure your IDE to detect the generated code directories.
     
1. Run `./gradlew build`   on Linux/Mac,  or `gradlew.bat build` on Windows to run a build.

1. Run `ServerMain` to start the gRPC server!

1. Run `ClientMain` to test some calls using the generated gRPC client

1. follow the steps below 

If you want to Run `./gradlew generateProto` generates sources from your .proto files  

## exposing the gRPC service as HTTP/JSON using Envoy proxy

Requirements:  
 * protoc (to generate a service definition that envoy understands)
 * docker (envoy comes in a docker container)

### Installing protoc
1. Goto: https://github.com/protocolbuffers/protobuf/releases/latest" +
2. download choose the precompiled version " +

       for linux:   protoc-3.6.1-linux-x86_64.zip" +
       for windows: protoc-3.6.1-win32.zip" +
       for mac:     protoc-3.6.1-osx-x86_64.zip" +

3. extract it somewhere in your PATH

### Installing docker

Check out this page: [Install docker](https://store.docker.com/search?offering=community&type=edition)

### Running Envoy to transcode our service

The script start-envoy.sh automates the tasks below for linux and mac:

    ./start-envoy.sh
    
#### Manual steps if script does not work 

1. Run the protoc command from within this project's root directory!
   notice that build/extracted-include-protos/main contains proto files from
   jar files on the classpath, for example: `com.google.api.grpc:googleapis-common-protos:0.0.3`
   this dependency contains the sources for `google.api.http` options we use
   in the .proto file
   
       protoc -I. -Ibuild/extracted-include-protos/main --include_imports --include_source_info --descriptor_set_out=reservation_service_definition.pb src/main/proto/reservation_service.proto
       
1. Run envoy (docker container) from within this directory

       docker run -it --rm --name envoy --network="host" -v "$(pwd)/reservation_service_definition.pb:/tmp/reservation_service_definition.pb:ro" -v "$(pwd)/envoy-config.yml:/etc/envoy/envoy.yaml:ro" envoyproxy/envoy   
 
### Testing the REST API 
  
1. Create a reservation

        curl -X POST \
          http://localhost:51051/v1/reservations \
          -H 'Content-Type: application/json' \
          -d '{
            "title": "Lunchmeeting2",
            "venue": "JDriven Coltbaan 3",
            "room": "atrium",
            "timestamp": "2018-10-10T11:12:13",
            "attendees": [
                {
                    "ssn": "1234567890",
                    "firstName": "Jimmy",
                    "lastName": "Jones"
                },
                {
                    "ssn": "9999999999",
                    "firstName": "Dennis",
                    "lastName": "Richie"
                }
            ]
        }'
        
   Example output:
   
   ```json
    {
        "id": "2cec91a7-d2d6-4600-8cc3-4ebf5417ac4b",
        "title": "Lunchmeeting2",
        "venue": "JDriven Coltbaan 3",
        "room": "atrium",
        "timestamp": "2018-10-10T11:12:13",
        "attendees": [
            {
                "ssn": "1234567890",
                "firstName": "Jimmy",
                "lastName": "Jones"
            },
            {
                "ssn": "9999999999",
                "firstName": "Dennis",
                "lastName": "Richie"
            }
        ]
    }
    ```     
        
        
1. Retrieve it (substitute ID in url): 

       curl -X GET http://localhost:51051/v1/reservations/<enter-id!!>

   example output          
    ```json
    {
        "id": "2cec91a7-d2d6-4600-8cc3-4ebf5417ac4b",
        "title": "Lunchmeeting2",
        "venue": "JDriven Coltbaan 3",
        "room": "atrium",
        "timestamp": "2018-10-10T11:12:13",
        "attendees": [
            {
                "ssn": "1234567890",
                "firstName": "Jimmy",
                "lastName": "Jones"
            },
            {
                "ssn": "9999999999",
                "firstName": "Dennis",
                "lastName": "Richie"
            }
        ]
    }        
    ```
          
1. Delete it (substitute ID in url): 

       curl -X DELETE http://localhost:51051/v1/reservations/<enter-id!!>
       
1. Create several reservations (vary the fields), and then list them with

       curl -X GET http://localhost:51051/v1/reservations
       
1. Then list them with a search on venue only

      curl -X GET "http://localhost:51051/v1/reservations?venue=JDriven%20Coltbaan%203"
      
   example output:
    ```json
    [
        {
            "id": "2cec91a7-d2d6-4600-8cc3-4ebf5417ac4b",
            "title": "Lunchmeeting2",
            "venue": "JDriven Coltbaan 3",
            "room": "atrium",
            "timestamp": "2018-10-10T11:12:13",
            "attendees": [
                {
                    "ssn": "1234567890",
                    "firstName": "Jimmy",
                    "lastName": "Jones"
                },
                {
                    "ssn": "9999999999",
                    "firstName": "Dennis",
                    "lastName": "Richie"
                }
            ]
        },
        {
            "id": "2f23c05a-c0ed-4b60-9b21-479d640030cc",
            "title": "Lunchmeeting",
            "venue": "JDriven Coltbaan 3",
            "timestamp": "2018-10-10T11:12:13",
            "attendees": [
                {
                    "ssn": "1234567890",
                    "firstName": "Jimmy",
                    "lastName": "Jones"
                },
                {
                    "ssn": "9999999999",
                    "firstName": "Dennis",
                    "lastName": "Richie"
                }
            ]
        }
    ]
    ```
      
  