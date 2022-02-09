package com.dyoung.router;

import com.dyoung.core.Utils;
import com.dyoung.core.handler.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
    private final Map<String, AsynchronousSocketChannel> routingTable;
    private final AsynchronousServerSocketChannel clientListener;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final Handler handler;
    private static String clientID;

    AcceptCompletionHandler(AsynchronousServerSocketChannel clientListener,
                            Map<String, AsynchronousSocketChannel> routingTable, Handler handler) {
        this.clientListener = clientListener;
        this.routingTable = routingTable;
        this.handler = handler;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, Void attachment) {
        if (clientListener.isOpen()) {
            clientListener.accept(null, this);
        }
        clientID = getID(channel);
        Utils.sendMessage(channel, clientID);
        routingTable.put(clientID, channel);
        printRoutingTable();
        ExecutorService service = Executors.newSingleThreadExecutor();
        while (true) {
            String message = Utils.readMessage(channel, buffer);
            if (message.equals("")) {
                clientID = getID(channel);
                break;
            }
            service.execute(() -> handler.handle(channel, message));
        }
        endConnection();
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        endConnection();
    }

    private void endConnection() {
        routingTable.remove(clientID);
        printRoutingTable();
    }

    private void printRoutingTable() {
        System.out.println("Routing table: " + routingTable.keySet());
    }

    private String getID(AsynchronousSocketChannel channel) {
        try {
            String remote = String.valueOf(channel.getRemoteAddress());
            String[] address = remote.split(":");
            int id = Integer.parseInt(address[1]);
            return String.format("%06d", id);
        } catch (IOException e) {
            System.out.println("Error - id");
        }
        return "123456";
    }
}
