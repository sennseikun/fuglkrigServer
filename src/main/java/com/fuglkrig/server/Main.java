package com.fuglkrig.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("FUGLKRIG server starting...");
        int port = 5555;

        //updates the ip address to the clients
        URL url = null;
        try {
            url = new URL("http://guttormsen.io/fuglkrig/updateip.php?code=fuGlKrig03042017'");
            Scanner s = new Scanner(url.openStream());
            System.out.println("updated ip address on server");
        } catch (Exception e) {
            System.out.println("couldnt update ip address");
            e.printStackTrace();
        }




        try {
            Thread t = new RequestHandler(port);
            t.start();

        }catch(IOException e) {
            e.printStackTrace();
        }

    }
}
