package com.dyoung.market.handler;

import com.dyoung.core.Utils;
import com.dyoung.core.handler.BaseHandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.Objects;

public class MetalHandler extends BaseHandler {
    private final String id;
    private final Map<String, Integer> metals;

    public MetalHandler(String id, Map<String, Integer> metals) {
        this.id = id;
        this.metals = metals;
    }

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        try {
            String metal = Utils.getTag(message, "55");
            int demandedQuantity = Integer.parseUnsignedInt(Objects.requireNonNull(Utils.getTag(message, "38")));
            if (demandedQuantity > 101 || demandedQuantity < 1)
                throw new NumberFormatException();
            if (metals.containsKey(metal)) {
                int availableQuantity = metals.get(metal);
                String side = Utils.getTag(message, "54");
                assert side != null;
                if (side.equals("1")) {
                    if (availableQuantity < demandedQuantity) {
                        String goter = Utils.getTag(message, "49");
                        Utils.sendMessage(clientChannel, Utils.statusOrder(id, goter, "8"));
                        System.out.println("Error - Demanded quantity is not available");
                        return;
                    } else {
                        metals.put(metal, availableQuantity - demandedQuantity);
                    }
                } else {
                    metals.put(metal, availableQuantity + demandedQuantity);
                }
                String goter = Utils.getTag(message, "49");
                Utils.sendMessage(clientChannel, Utils.statusOrder(id, goter, "2"));
                System.out.println("Trading: " + metals);
            } else {
                String goter = Utils.getTag(message, "49");
                Utils.sendMessage(clientChannel, Utils.statusOrder(id, goter, "8"));
                System.out.println("Error - " + metal + " is not traded on the market");
            }
        } catch (NumberFormatException ex) {
            String goter = Utils.getTag(message, "49");
            Utils.sendMessage(clientChannel, Utils.statusOrder(id, goter, "8"));
            System.out.println("Error - Wrong value quantity");
        }
    }
}
