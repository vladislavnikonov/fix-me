package com.dyoung.core.handler;

import java.nio.channels.AsynchronousSocketChannel;

public class ErrorHandler extends BaseHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        if (!message.startsWith("Error")) {
            super.handle(clientChannel, message);
        }
    }
}
