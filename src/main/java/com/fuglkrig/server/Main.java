package com.fuglkrig.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("FUGLKRIG server starting...");
        int port = 5555;

        //updates the ip address to the clients
        /*URL url = null;
        try {
            url = new URL("http://guttormsen.io/fuglkrig/updateip.php?code=fuGlKrig03042017'");
            Scanner s = new Scanner(url.openStream());
            System.out.println("updated ip address on server");
        } catch (Exception e) {
            System.out.println("couldnt update ip address");
            e.printStackTrace();
        }*/

        URL url = null;

        /**
         * gets ip address to server
         */
        try {
            String code = "fuGlKrig03042017";
            url = new URL("http://guttormsen.io/fuglkrig/updateip.php?code=" + URLEncoder.encode(code, "UTF-8"));

            Scanner s = new Scanner(url.openStream());
            System.out.println("information from server");
            System.out.println(s.nextLine());

        } catch (Exception e) {
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
