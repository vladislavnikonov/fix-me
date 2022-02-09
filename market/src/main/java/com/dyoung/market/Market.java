package com.dyoung.market;

import com.dyoung.core.Client;
import com.dyoung.core.handler.Handler;
import com.dyoung.market.handler.MetalHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Market extends Client {
    private final Map<String, Integer> quantityMetals = new HashMap<>();
    public static final String[] METALS = {
            "gold", "serebro", "platinum", "palladium"
    };

    @Override
    protected void start(int port) {
        super.start(port);
        Random random = new Random();
        for (String metal : METALS)
            quantityMetals.put(metal, random.nextInt(20) + 1);
        System.out.println("Trading: " + quantityMetals);
        while (true) ;
    }

    @Override
    protected Handler getHandler() {
        Handler handler = super.getHandler();
        Handler metalHandler = new MetalHandler(getId(), quantityMetals);
        handler.setNext(metalHandler);
        return handler;
    }

    public static void main(String[] args) {
        new Market().start(5001);
    }
}
