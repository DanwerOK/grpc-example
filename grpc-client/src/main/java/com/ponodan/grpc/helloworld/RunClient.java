package com.ponodan.grpc.helloworld;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunClient {
    private static final Logger LOGGER = Logger.getLogger(RunClient.class.getName());
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 50051;

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    private RunClient(String host, int port) {
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                //.usePlaintext()
                .build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws Exception {
        RunClient client = new RunClient(SERVER_ADDRESS, SERVER_PORT);

        // If provided, the first element of {@code args} is the name to use in the greeting.
        try {
            String user = "world";
            if (args.length > 0) user = args[0];
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }

    private void greet(String name) {
        LOGGER.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            LOGGER.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        LOGGER.info("Greeting: " + response.getMessage());
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
