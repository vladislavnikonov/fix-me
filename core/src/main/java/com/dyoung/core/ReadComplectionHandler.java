package com.dyoung.core;

import com.dyoung.core.handler.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ReadComplectionHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel socketChannel;
    private final Handler handler;
    private final ByteBuffer buffer;

    ReadComplectionHandler(AsynchronousSocketChannel socketChannel, Handler handler, ByteBuffer buffer) {
        this.socketChannel = socketChannel;
        this.handler = handler;
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, Void attachment) {
        String message = Utils.gotMessage(buffer);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> handler.handle(socketChannel, message));
        socketChannel.read(buffer, null, this);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        endConnection();
    }

    private void endConnection() {
        System.out.println("Router not responding");
        System.out.println("Update router and client");
        try {
            socketChannel.close();
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error - close");
        }
    }
}
