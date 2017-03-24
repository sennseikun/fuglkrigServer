package com.fuglkrig.server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("FUGLKRIG server starting...");
        int port = 5555;
        try {
            Thread t = new RequestHandler(port);
            t.start();

        }catch(IOException e) {
            e.printStackTrace();
        }

    }
}
