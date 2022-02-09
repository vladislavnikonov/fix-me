package com.dyoung.router.handler;

import com.dyoung.core.Utils;
import com.dyoung.core.handler.BaseHandler;

import java.nio.channels.AsynchronousSocketChannel;

public class ChecksumHandler extends BaseHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        String messageChecksum = Utils.getTag(message, "10");
        String newChecksum = Utils.createChecksum(message, message.length() - 7);
        if (newChecksum.equals(messageChecksum)) {
            super.handle(clientChannel, message);
        } else {
            Utils.sendMessage(clientChannel, "Error - Invalid checksum");
        }
    }
}
