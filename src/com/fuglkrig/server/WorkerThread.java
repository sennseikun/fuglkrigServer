package com.fuglkrig.server;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 3/26/2017.
 */
public class WorkerThread implements Runnable {

    private String command;
    private Socket socket;
    private JSONObject json;
    private String message;
    private int id;
    private ReceiveThread ReceiveThread;

    public WorkerThread(ReceiveThread ReceiveThread ,int id, String command, Socket socket, String message){
        this.command=command;
        this.socket = socket;
        this.json = json;
        this.message = message;
        this.id = id;
        this.ReceiveThread = ReceiveThread;
    }

    public WorkerThread(String command){
        this.command = command;
    }

    public void sendLobbys(){

        try {

            //Lobby logic here
            JSONObject json = new JSONObject();
            json.put("Datatype", "1");

            //Remove the lobbies without users in it

            //LobbyList.remove_empty_lists();

            List<Lobby> lobbys = LobbyList.getLobbys();

            json.put("Count", lobbys.size());

            //Return all the lobbies with lobby name, player count, max player count and password

            for (int i = 1; i < lobbys.size() + 1; i++) {

                Lobby l = lobbys.get(i - 1);

                System.out.println("Packing lobby: " + l.getName());

                json.put("Name" + i, l.getName());
                json.put("PlayerCount" + i, l.getPlayerCount());
                json.put("MaxPlayers" + i, l.getMax_player_count());
                json.put("Password" + i, l.getPassword());
            }

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ReceiveThread: Sent lobbylist");
    }

    @Override
    public void run() {

        try {

            //CHECK if player can be made

            if (command.equals("1")) {
                String name;
                JSONObject json = new JSONObject(message);
                name = json.getString("nick");
                String value = "1";
                ArrayList<Player> op = OnlinePlayers.getPlayers();

                //Checks if name is empty

                if (name.equals("")) {
                    value = "0";
                }

                //Checks if other users already has this username

                else if (op != null) {
                    for (int i = 0; op.size() < i; i++) {
                        if (op.get(i).getNick().equals(name)) {
                            value = "0";
                        }
                    }
                }

                //Create and send answer to send back to client

                JSONObject retur = new JSONObject();
                retur.put("Datatype", "0");
                retur.put("userValid", value);
                retur.put("pId", id);
                retur.put("nick", name);

                if (value.equals("1")) {

                    Player player = new Player(name, id, 0, socket);
                    ReceiveThread.setPlayer(player);
                    OnlinePlayers.newPlayer(player);

                    System.out.println("Players online: " + OnlinePlayers.getPlayers());
                }

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(retur.toString());

                System.out.println("ReceiveThread: Sent user verification");

            }

            //Return all lobbies

            else if (command.equals("2")) {
                sendLobbys();
            }

            //Create new lobby

            else if (command.equals("3")) {

                //Setup a new lobby and put the creating player in it

                JSONObject jsonObject = new JSONObject(message);
                String name = jsonObject.getString("Name");
                String playerName = jsonObject.getString("PlayerName");
                int max_players = jsonObject.getInt("Players");
                String password = jsonObject.getString("Password");

                List<Player> playerList = new ArrayList<>();
                Player player = new Player(playerName,id,0,socket);

                playerList.add(player);

                Lobby lobby = new Lobby(name,playerList,max_players,password);

                System.out.println("ReceiveThread: Creating a lobby and putting it in the list");
                LobbyList.addLobby(lobby);

                JSONObject sendJson = new JSONObject();

                sendJson.put("Datatype","2");
                sendJson.put("Valid","1");

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());

            }

            //Withdraw from lobby

            else if (command.equals("4")) {

                JSONObject jsonObject = new JSONObject(message);
                String name = jsonObject.getString("Name");
                String lobbyID = jsonObject.getString("Lobby");

                List<Lobby> lobbys = LobbyList.getLobbys();
                Lobby currLobby = null;

                for(Lobby l: lobbys){
                    if(l.getName().equals(lobbyID)){
                        l.removeByPlayerName(name);
                        currLobby = l;
                        break;
                    }
                }

                LobbyList.remove_empty_lists();

                String playerCount;
                JSONObject sendJson = new JSONObject();

                if(currLobby != null){

                    //WRITE

                    playerCount = Integer.toString(currLobby.getPlayers().size());
                    System.out.println("ReceiveThread: Removed player: "+name);

                    System.out.println("ReceiveThread: Player count: "+playerCount);

                    sendJson.put("Datatype","3");
                    sendJson.put("LobbyID",lobbyID);
                    sendJson.put("Error","0");
                    sendJson.put("PlayerCount",playerCount);
                    sendJson.put("PlayerName",name);
                }
                else{
                    sendJson.put("Datatype","3");
                    sendJson.put("Error","1");
                }

                LobbyList.remove_empty_lists();


                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());

            }

            //Join a new lobby

            else if(command.equals("5")){
                JSONObject jsonObject = new JSONObject(message);
                String name = jsonObject.getString("Name");
                String lobbyID = jsonObject.getString("Lobby");

                List<Lobby> lobbys = LobbyList.getLobbys();
                Lobby currLobby = null;
                List<Player> players = null;
                Player currPlayer = null;

                //Add player to the lobby being joined

                for(Lobby l: lobbys){
                    if(l.getName().equals(lobbyID)){

                        Player p = OnlinePlayers.getPlayer(name);
                        currPlayer = p;

                        players = l.getPlayers();

                        l.addPlayer(p);
                        currLobby = l;
                        break;
                    }
                }

                if(players != null){
                    System.out.println("Players" + players);
                    for(Player p: players){
                        if(!p.toString().equals(currPlayer.toString())){

                            JSONObject sendJson = new JSONObject();

                            sendJson.put("Datatype","4");
                            sendJson.put("LobbyID",lobbyID);
                            sendJson.put("Error","0");
                            sendJson.put("PlayerName",name);
                            sendJson.put("PlayerCount",Integer.toString(players.size()));

                            //

                        }
                    }
                }


                String playerCount;

                JSONObject sendJson = new JSONObject();

                if(currLobby != null){
                    playerCount = Integer.toString(currLobby.getPlayers().size());
                    System.out.println("Added player: "+name);

                    sendJson.put("Datatype","4");
                    sendJson.put("LobbyID",lobbyID);
                    sendJson.put("Error","0");
                    sendJson.put("PlayerName",name);
                    sendJson.put("PlayerCount",playerCount);

                }
                else{
                    sendJson.put("Datatype","4");
                    sendJson.put("Error","1");
                }

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());

            }

            //Remove player

            else if(command.equals("6")){
                JSONObject json = new JSONObject(message);
                String name = json.getString("nick");
                String status = "0";

                if(OnlinePlayers.hasPlayer(name)){

                    Player p = OnlinePlayers.getPlayer(name);

                    OnlinePlayers.removePlayer(OnlinePlayers.getPlayer(name));
                    System.out.println("Removed from OnlinePlayers: "+name);
                    status = "1";

                    for(Lobby l: LobbyList.getLobbys()){
                        if(l.containsPlayer(p)){
                            l.removePlayer(p);
                            System.out.println("Player: " + name + " removed");
                        }
                    }

                }

                JSONObject sendJson = new JSONObject();
                sendJson.put("Datatype","5");
                sendJson.put("Status",status);

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());
            }
            else if(command.equals("7")){
                List<Player> players = OnlinePlayers.getPlayers();

                if(!players.isEmpty()){
                    for(Player p: players){
                        if(p.getPlayerSocket().isClosed()){
                            OnlinePlayers.removePlayer(p);
                        }
                    }
                }
                LobbyList.remove_empty_lists();

                System.out.println("Current lobbies: " + LobbyList.getLobbys().size());

            }

            else{
                ReceiveThread.stopConnection();
                ReceiveThread.stopThread();
            }

        } catch (IOException e) {
            e.printStackTrace();
            ReceiveThread.stopConnection();
            ReceiveThread.stopThread();
        }
    }
    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return command;
    }
}
