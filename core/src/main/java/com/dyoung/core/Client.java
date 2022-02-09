package com.dyoung.core;

import com.dyoung.core.handler.ErrorHandler;
import com.dyoung.core.handler.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class Client {
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private AsynchronousSocketChannel socketChannel;
    private String id;
    private int port;

    private AsynchronousSocketChannel connect() {
        final AsynchronousSocketChannel socketChannel;
        try {
            socketChannel = AsynchronousSocketChannel.open();
            final Future<Void> future = socketChannel.connect(new InetSocketAddress("127.0.0.1", port));
            future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Connecting to router...");
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException ignored) {
            }
            return connect();
        }
        return socketChannel;
    }

    protected void start(int port) {
        this.port = port;
        socketChannel = connect();
        id = Utils.readMessage(socketChannel, buffer);
        socketChannel.read(buffer, null,
                new ReadComplectionHandler(socketChannel, getHandler(), buffer));
    }

    protected Handler getHandler() {
        return new ErrorHandler();
    }

    protected AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    protected String getId() {
        return id;
    }
}
