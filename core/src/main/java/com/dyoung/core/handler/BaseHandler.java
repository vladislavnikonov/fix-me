package com.dyoung.core.handler;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class BaseHandler implements Handler {

    private Handler next;

    @Override
    public final void setNext(Handler next) {
        this.next = next;
    }

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        if (next != null) {
            next.handle(clientChannel, message);
        }
    }
}
