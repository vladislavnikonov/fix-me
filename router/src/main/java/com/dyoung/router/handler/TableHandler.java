package com.dyoung.router.handler;

import com.dyoung.core.Utils;
import com.dyoung.core.handler.BaseHandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

public class TableHandler extends BaseHandler {

    private final Map<String, AsynchronousSocketChannel> routingTable;

    public TableHandler(Map<String, AsynchronousSocketChannel> routingTable) {
        this.routingTable = routingTable;
    }

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        final String goter = Utils.getTag(message, "56");
        final AsynchronousSocketChannel targetChannel = routingTable.get(goter);
        if (targetChannel != null) {
            Utils.sendMessage(targetChannel, message);
        } else {
            Utils.sendMessage(clientChannel, "Error - Client isn't available");
        }
    }
}
