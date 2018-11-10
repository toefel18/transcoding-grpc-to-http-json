#!/usr/bin/env bash

if ! [ -x "$(command -v protoc)" ] ; then
    echo "you do not seem to have the protoc executable on your path"
    echo "we need protoc to generate a service defintion (*.pb file) that envoy can understand"
    echo "download the precompiled protoc executable and place it in somewhere in your systems PATH!"
    echo "goto: https://github.com/protocolbuffers/protobuf/releases/latest"
    echo "choose:"
    echo "       for linux:   protoc-3.6.1-linux-x86_64.zip"
    echo "       for windows: protoc-3.6.1-win32.zip"
    echo "       for mac:     protoc-3.6.1-osx-x86_64.zip"
    exit 1
fi

# generate the reservation_service_definition.pb file that we can pass to envoy so that knows the grpc service
# we want to expose
protoc -I. -Ibuild/extracted-include-protos/main --include_imports \
                --include_source_info \
                --descriptor_set_out=reservation_service_definition.pb \
                src/main/proto/reservation_service.proto

if ! [ $? -eq 0 ]; then
    echo "protobuf compilation failed"
    exit 1
fi

# now we can start envoy in a docker container and map the configuration and service definition inside
# we use --network="host" so that envoy can access the grpc service at localhost:<port>
# the envoy-config.yml has configured envoy to run at port 51051, so you can access the HTTP/JSON
# api at localhost:51051


if ! [ -x "$(command -v docker)" ] ; then
    echo "docker command is not available, please install docker"
    echo "Install docker: https://store.docker.com/search?offering=community&type=edition"
    exit 1
fi

# check if sudo is required to run docker
if [ "$(groups | grep -c docker)" -gt "0" ]; then
    echo "Envoy will run at port 51051 (see envoy-config.yml)"
    docker run -it --rm --name envoy --network="host" \
             -v "$(pwd)/reservation_service_definition.pb:/data/reservation_service_definition.pb:ro" \
             -v "$(pwd)/envoy-config.yml:/etc/envoy/envoy.yaml:ro" \
             envoyproxy/envoy
else
    echo "you are not in the docker group, running with sudo"
    echo "Envoy will run at port 51051 (see envoy-config.yml)"
    sudo docker run -it --rm --name envoy --network="host"\
             -v "$(pwd)/reservation_service_definition.pb:/data/reservation_service_definition.pb:ro" \
             -v "$(pwd)/envoy-config.yml:/etc/envoy/envoy.yaml:ro" \
             envoyproxy/envoy
fi


