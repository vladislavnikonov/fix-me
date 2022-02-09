package com.dyoung.core.handler;

import java.nio.channels.AsynchronousSocketChannel;

public interface Handler {

    void setNext(Handler handler);

    void handle(AsynchronousSocketChannel clientChannel, String message);
}
