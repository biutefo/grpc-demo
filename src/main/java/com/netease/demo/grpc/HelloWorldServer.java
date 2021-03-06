package com.netease.demo.grpc;


import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;


/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */

public class HelloWorldServer {

    private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());


    private Server server;


    private void start() throws IOException {

        /* The port on which the server should run */

        int port = 55055;

        server = ServerBuilder.forPort(port)

                .addService((BindableService) new GreeterImpl())

                .build()

                .start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override

            public void run() {

                // Use stderr here since the logger may have been reset by its JVM shutdown hook.

                System.err.println("*** shutting down gRPC server since JVM is shutting down");

                HelloWorldServer.this.stop();

                System.err.println("*** server shut down");

            }

        });

    }


    private void stop() {

        if (server != null) {

            server.shutdown();

        }

    }


    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */

    private void blockUntilShutdown() throws InterruptedException {

        if (server != null) {

            server.awaitTermination();

        }

    }


    /**
     * Main launches the server from the command line.
     */

    public static void main(String[] args) throws IOException, InterruptedException {

        final HelloWorldServer server = new HelloWorldServer();

        server.start();

        server.blockUntilShutdown();

    }


    static class GreeterImpl extends helloworld.GreeterGrpc.GreeterImplBase {


        @Override

        public void sayHello(helloworld.HelloRequest req, StreamObserver<helloworld.HelloReply> responseObserver) {

            helloworld.HelloReply reply = helloworld.HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();

            responseObserver.onNext(reply);

            responseObserver.onCompleted();

        }

    }

}