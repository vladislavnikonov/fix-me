package com.dyoung.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Utils {
    private static final String SENDER = "49";
    private static final String GOTER = "56";
    private static final String SIDE = "54";
    private static final String METAL = "55";
    private static final String QUANTITY = "38";
    private static final String PRICE = "44";
    private static final String STATUS = "39";
    private static final String CHECKSUM = "10";

    public static String createOrder(String input, String id) {
        String[] tags = input.split(" ");
        if (tags.length != 4 || (!tags[1].equals("buy") && !tags[1].equals("sell"))) {
            System.out.println("Error input");
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(SENDER + "=").append(id)
                .append("|" + GOTER + "=").append(tags[0])
                .append("|" + SIDE + "=").append(tags[1].equals("buy") ? "1" : "2")
                .append("|" + METAL + "=").append(tags[2])
                .append("|" + QUANTITY + "=").append(tags[3])
                .append("|" + PRICE + "=1|");
        String checksum = createChecksum(builder.toString(), builder.length());
        builder.append(CHECKSUM + "=").append(checksum).append("|");
        return builder.toString();
    }

    public static String statusOrder(String id, String goter, String status) {
        StringBuilder builder = new StringBuilder();
        builder.append(SENDER + "=").append(id)
                .append("|" + GOTER + "=").append(goter)
                .append("|" + STATUS + "=").append(status).append("|");
        String checksum = createChecksum(builder.toString(), builder.length());
        builder.append(CHECKSUM + "=").append(checksum).append("|");
        return builder.toString();
    }

    public static String createChecksum(String message, int len) {
        int sum = 0;
        for (int i = 0; i < len; i += 1) {
            sum += (message.charAt(i));
        }
        return String.format("%03d", sum % 256);
    }

    public static String getTag(String fixMessage, String tag) {
        String[] tags = fixMessage.split("\\|");
        for (String parts : tags) {
            String[] values = parts.split("=");
            if (Objects.equals(values[0], tag))
                return values[1];
        }
        return null;
    }

    public static void sendMessage(AsynchronousSocketChannel channel, String message) {
        byte[] byteMsg = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        Future<Integer> writeResult = channel.write(buffer);
        try {
            writeResult.get();
            System.out.println("Send: " + message);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error - send");
        }
    }

    public static String readMessage(AsynchronousSocketChannel channel, ByteBuffer buffer) {
        Future<Integer> readResult = channel.read(buffer);
        try {
            readResult.get();
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
        return gotMessage(buffer);
    }

    public static String gotMessage(ByteBuffer buffer) {
        String message = new String(buffer.array()).trim();
        buffer.clear();
        buffer.put(new byte[1024]);
        buffer.clear();
        System.out.println("Got: " + message);
        return message;
    }
}
