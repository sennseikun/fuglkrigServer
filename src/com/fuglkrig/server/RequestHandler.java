package com.fuglkrig.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Simon on 16.03.2017.
 */
public class RequestHandler extends Thread {
    private ServerSocket serverSocket;
    private Socket server;
    private int id;

    public RequestHandler(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        id = 0;
    }

    public void run() {
        while (true) {
            try {

                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                ReceiveThread receiveThread = new ReceiveThread(server,id);
                receiveThread.start();

                id++;

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}