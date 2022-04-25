package com.netease.demo.grpc;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */

public class HelloWorldClient {

    private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());


    private final ManagedChannel channel;

    private final pers.xin.demo.HelloServiceGrpc.HelloServiceBlockingStub blockingStub;


    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */

    public HelloWorldClient(String host, int port) {

        this(ManagedChannelBuilder.forAddress(host, port)

                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid

                // needing certificates.

                .usePlaintext()

                .build());

    }


    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */

    HelloWorldClient(ManagedChannel channel) {

        this.channel = channel;

        blockingStub = pers.xin.demo.HelloServiceGrpc.newBlockingStub(channel);

    }


    public void shutdown() throws InterruptedException {

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);

    }


    /**
     * Say hello to server.
     */

    public void greet(String name) {

        logger.info("Will try to greet " + name + " ...");

        pers.xin.demo.HelloRequest request = pers.xin.demo.HelloRequest.newBuilder().setName(name).build();

        pers.xin.demo.HelloReply response;

        try {

            response = blockingStub.greeting(request);

        } catch (StatusRuntimeException e) {

            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            e.printStackTrace();
            return;

        }

        logger.info("Greeting: " + response.getMessage());

    }


    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * <p>
     * greeting.
     */

    public static void main(String[] args) throws Exception {

        HelloWorldClient client = new HelloWorldClient("970121.xyz", 55055);

        try {

            /* Access a service running on the local machine on port 50051 */

            String user = "world";

            if (args.length > 0) {

                user = args[0]; /* Use the arg as the name to greet if provided */

            }

            client.greet(user);

        } finally {

            client.shutdown();

        }

    }

}