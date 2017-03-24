package com.fuglkrig.server;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;


/**
 * Created by thoma on 3/23/2017.
 */
public class ReceiveThread extends Thread {

    Socket inputSocket;
    int id;


    public ReceiveThread(Socket inputSocket, int id){
        this.inputSocket = inputSocket;
        this.id = id;
    }


    @Override
    public void run(){
        while(true){
            try {

                System.out.println("ID"+id);

                DataInputStream in;
                in = new DataInputStream(inputSocket.getInputStream());
                String message = in.readUTF();


                System.out.println(message);
                message = in.readUTF();

                System.out.println(message);

                DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                out.writeUTF("Message accepted" + inputSocket.getLocalSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
