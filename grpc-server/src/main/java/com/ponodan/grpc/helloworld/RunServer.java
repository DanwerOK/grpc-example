package com.ponodan.grpc.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class RunServer {
    private static final Logger LOGGER = Logger.getLogger(RunServer.class.getName());
    private static final int PORT = 50051;
    
    private static Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final RunServer server = new RunServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        server = ServerBuilder
                .forPort(PORT)
                .addService(new GreeterImpl())
                .build()
                .start();
        LOGGER.info("Server started, listening on " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the LOGGER may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                RunServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}