package com.dyoung.router;

import com.dyoung.core.handler.Handler;
import com.dyoung.router.handler.ChecksumHandler;
import com.dyoung.router.handler.TableHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, AsynchronousSocketChannel> routingTable = new HashMap<>();

    private void start() {
        Handler checksumHandler = new ChecksumHandler();
        Handler tableHandler = new TableHandler(routingTable);
        checksumHandler.setNext(tableHandler);
        try {
            AsynchronousServerSocketChannel brokersListener = AsynchronousServerSocketChannel.open()
                    .bind(new InetSocketAddress("127.0.0.1", 5000));
            brokersListener.accept(null,
                    new AcceptCompletionHandler(brokersListener, routingTable, checksumHandler));

            AsynchronousServerSocketChannel marketsListener = AsynchronousServerSocketChannel.open()
                    .bind(new InetSocketAddress("127.0.0.1", 5001));
            marketsListener.accept(null,
                    new AcceptCompletionHandler(marketsListener, routingTable, checksumHandler));
            System.out.println("Router is listening at ports 5000|5001");
        } catch (IOException e) {
            System.out.println("Router couldn't open socket");
        }
        while (true) ;
    }

    public static void main(String[] args) {
        new Router().start();
    }
}
