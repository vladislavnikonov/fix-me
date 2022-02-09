package com.dyoung.broker;

import com.dyoung.broker.handler.StatusHandler;
import com.dyoung.core.Client;
import com.dyoung.core.Utils;
import com.dyoung.core.handler.Handler;

import java.util.Scanner;

public class Broker extends Client {

    @Override
    protected void start(int port) {
        super.start(port);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter: market buy|sell metal quantity(1-100)");
        while (true) {
            final String message = Utils.createOrder(scanner.nextLine(), getId());
            if (!message.equals(""))
                Utils.sendMessage(getSocketChannel(), message);
        }
    }

    @Override
    protected Handler getHandler() {
        Handler handler = super.getHandler();
        Handler resultTag = new StatusHandler();
        handler.setNext(resultTag);
        return handler;
    }

    public static void main(String[] args) {
        new Broker().start(5000);
    }
}
