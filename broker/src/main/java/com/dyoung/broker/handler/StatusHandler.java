package com.dyoung.broker.handler;

import com.dyoung.core.Utils;
import com.dyoung.core.handler.BaseHandler;

import java.nio.channels.AsynchronousSocketChannel;

public class StatusHandler extends BaseHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        String result = Utils.getTag(message, "39");
        if (result == null) {
            System.out.println("Client isn't market");
            return;
        }
        System.out.println(result.equals("2") ? "Executed" : "Rejected");
    }
}
