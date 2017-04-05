package com.fuglkrig.server;

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
    private ReceiveThread receiveThread;

    /**
     * Creating the thread that is running the game
     * @param receiveThread
     * @param id
     * @param command
     * @param socket
     * @param message
     */
    public WorkerThread(ReceiveThread receiveThread ,int id, String command, Socket socket, String message){

        this.command=command;
        this.socket = socket;
        this.json = json;
        this.message = message;
        this.id = id;
        this.receiveThread = receiveThread;
    }

    public WorkerThread(String command){
        this.command = command;
    }



    @Override
    public void run() {

        try {

            //CHECK if player can be made

            if (command.equals("0")) {
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
                    receiveThread.setPlayer(player);
                    OnlinePlayers.newPlayer(player);

                    System.out.println("WorkerThread 0: Players online: " + OnlinePlayers.getPlayers());
                }

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(retur.toString());

                System.out.println("WorkerThread 0: ReceiveThread: Sent user verification");

            }

            //Return all lobbies

            else if (command.equals("1")) {
                try {

                    int count = 0;

                    //Lobby logic here
                    JSONObject json = new JSONObject();
                    json.put("Datatype", "1");

                    List<Lobby> lobbys = LobbyList.getLobbys();

                    //Return all the lobbies with lobby name, player count, max player count and password

                    for (int i = 1; i < lobbys.size() + 1; i++) {

                        Lobby l = lobbys.get(i - 1);

                        if(l.isStarted()){
                            System.out.println("Game started not sent");
                        }

                        else{

                            count++;

                            System.out.println("WorkerThread 2: Packing lobby: " + l.getName());

                            json.put("Name" + i, l.getName());
                            json.put("PlayerCount" + i, l.getPlayerCount());
                            json.put("MaxPlayers" + i, l.getMax_player_count());

                            if(l.getPassword().equals("")){
                                json.put("Password" + i, "");
                            }
                            else{
                                json.put("Password" + i, "1");
                            }
                        }
                    }

                    json.put("Count", count);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("ReceiveThread: Sent lobbylist");
            }

            //Create new lobby

            else if (command.equals("2")) {

                //Setup a new lobby and put the creating player in it

                JSONObject jsonObject = new JSONObject(message);
                String name = jsonObject.getString("Name");
                String playerName = jsonObject.getString("PlayerName");
                int max_players = jsonObject.getInt("Players");
                String password = jsonObject.getString("Password");

                List<Player> playerList = new ArrayList<>();
                Player player = OnlinePlayers.getPlayer(playerName);

                playerList.add(player);

                Lobby lobby = new Lobby(name,playerList,max_players,password);

                System.out.println("WorkerThread 2: ReceiveThread: Creating a lobby and putting it in the list");
                LobbyList.addLobby(lobby);

                JSONObject sendJson = new JSONObject();

                sendJson.put("Datatype","2");
                sendJson.put("Valid","1");

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());

            }

            //Withdraw from lobby

            else if (command.equals("3")) {

                JSONObject jsonObject = new JSONObject(message);
                String name = jsonObject.getString("Name");
                String lobbyID = jsonObject.getString("Lobby");

                List<Lobby> lobbys = LobbyList.getLobbys();
                List<Player> players;
                Lobby currLobby = null;

                for(Lobby l: lobbys){
                    if(l.getName().equals(lobbyID)){
                        currLobby = l;
                        break;
                    }
                }

                players = currLobby.getPlayers();

                for(Player p: players){
                    p.removeFromGameLobby(name,lobbyID,players);
                }
                currLobby.removeByPlayerName(name);
                LobbyList.updateLobbies();
                LobbyList.remove_empty_lists();

            }

            //Join a new lobby

            else if(command.equals("4")){
                JSONObject jsonObject = new JSONObject(message);
                String name = jsonObject.getString("Name");
                String lobbyID = jsonObject.getString("Lobby");

                if(LobbyList.getLobby(lobbyID) != null && !LobbyList.getLobby(lobbyID).isStarted()) {

                    int playerCount = LobbyList.getLobby(lobbyID).getPlayerCount();
                    int maxPlayerCount = LobbyList.getLobby(lobbyID).getMax_player_count();

                    for (Player p : LobbyList.getLobby(lobbyID).getPlayers()) {
                    }


                    if (playerCount >= maxPlayerCount) {

                        JSONObject fullJson = new JSONObject();
                        fullJson.put("Datatype", 9);

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(fullJson.toString());
                    } else {
                        List<Lobby> lobbys = LobbyList.getLobbys();
                        Lobby currLobby = null;
                        List<Player> players = null;

                        //Add player to the lobby being joined

                        for (Lobby l : lobbys) {
                            if (l.getName().equals(lobbyID)) {

                                Player p = OnlinePlayers.getPlayer(name);
                                l.addPlayer(p);
                                currLobby = l;
                                players = l.getPlayers();

                                break;
                            }
                        }

                        if (players != null && currLobby != null) {


                            for (Player p : players) {
                                p.addToGameLobby(lobbyID, name, players);
                            }
                        } else {
                            JSONObject fullJson = new JSONObject();
                            fullJson.put("Datatype", 9);

                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF(fullJson.toString());
                        }
                        LobbyList.updateLobbies();
                    }
                }
                else {

                    JSONObject fullJson = new JSONObject();
                    fullJson.put("Datatype", 20);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(fullJson.toString());
                }
            }

            //Remove player

            else if(command.equals("5")){
                JSONObject json = new JSONObject(message);
                String name = json.getString("nick");
                String status = "0";

                if(OnlinePlayers.hasPlayer(name)){

                    Player p = OnlinePlayers.getPlayer(name);

                    OnlinePlayers.removePlayer(OnlinePlayers.getPlayer(name));
                    status = "1";

                    for(Lobby l: LobbyList.getLobbys()){
                        if(l.containsPlayer(p)){
                            l.removePlayer(p);
                        }
                    }

                }

                JSONObject sendJson = new JSONObject();
                sendJson.put("Datatype","5");
                sendJson.put("Status",status);

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());
            }
            else if(command.equals("6")){
                /*List<Player> players = OnlinePlayers.getPlayers();

                if(!players.isEmpty()){
                    for(Player p: players){
                        if(p.getPlayerSocket().isClosed()){
                            OnlinePlayers.removePlayer(p);
                        }
                    }
                }
                LobbyList.updateLobbies();
                LobbyList.remove_empty_lists();*/


            }

            //Password check

            else if(command.equals("7")){

                JSONObject receivedJson = new JSONObject(message);

                String name = receivedJson.getString("GameName");
                String pass = receivedJson.getString("Password");
                String status = "0";

                if(LobbyList.getLobby(name).getPassword().equals(pass)){
                    status = "1";
                }

                JSONObject sendJson = new JSONObject();


                sendJson.put("Datatype","8");
                sendJson.put("Status",status);

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(sendJson.toString());
            }

            else if(command.equals("11")) {

                /**
                 *   Sjekk om spilleren er først i lobby sin spiller liste, hvis ja, start spill
                 *  hvis nei, break.
                 */

                List<Player> playerList = LobbyList.getPlayersFromLobby(receiveThread.getPlayer());
                List<Player> players = LobbyList.getLobbyWithPlayer(receiveThread.getPlayer()).getPlayers();
                Lobby l = LobbyList.getLobbyWithPlayer(receiveThread.getPlayer());

                for(Player p: playerList){
                    System.out.println("Host player in current lobby: " + p.getNick());
                }

                for(Lobby l1: LobbyList.getLobbys()){
                    System.out.println("Lobbies active: " + l1.getName());
                }

                for(Player p: players){
                    System.out.println("All players in lobby: " + p.getNick());
                }

                if (playerList != null && players.size() > 1) {
                    Game game = new Game(playerList);

                    //makes sure players have a game
                    for (Player player : game.getPlayers()) {
                        player.setCurrentGame(game);
                    }
                    l.setStarted(true);

                    LobbyList.removeLobby(l);
                    game.start();
                }
            }

            else if(command.equals("12")) {
                JSONObject jsonData = new JSONObject(message);

                double x = jsonData.getDouble("TargetX");
                double y = jsonData.getDouble("TargetY");

                if (x >= 0) {
                    this.receiveThread.getPlayer().setTargetPosX(x);
                }
                if(y >= 0){
                    this.receiveThread.getPlayer().setTargetPosY(y);
                }
                this.receiveThread.getPlayer().UpdateDxDy();
            }

            else if(command.equals("13")){
                JSONObject jsonData = new JSONObject(message);
                int type = jsonData.getInt("Type");
                Player player = receiveThread.getPlayer();

                System.out.println("type: " + type);
                System.out.println(player.getPowerups());
                System.out.println(player.getPowerups().contains(Integer.valueOf(type)));

                //checks if the user actually got the powerup he wants to use
                if (player.getPowerups().contains(Integer.valueOf(type))) {
                    System.out.println("spawning powerup");
                    //removes the powerup from the user
                    player.getPowerups().remove(Integer.valueOf(type));
                    //spawn the powerup
                    player.getCurrentGame().addPowerup(type, (int) player.getCoordX() - 50, (int) player.getCoordY());
                }
                else {
                    System.out.println("user cheating? tried using a powerup he doesnt have");
                }
            }

            //Clean up after a game, while not ending connection?

            else if(command.equals("14")){
                Lobby removeLobby = LobbyList.getLobbyWithPlayer(this.receiveThread.getPlayer());
                System.out.println("Cleaning up after game");
                if(removeLobby != null){

                    System.out.println("Found removelobby" + removeLobby.getName());

                    for(Player p : removeLobby.getPlayers()){
                        if(!p.getNick().equals(this.receiveThread.getPlayer().getNick())){
                            System.out.println("Sending remove request to " + p.getNick());
                            p.removeFromGameLobby(this.receiveThread.getPlayer().getNick(),removeLobby.getName(),removeLobby.getPlayers());
                        }
                    }
                }
                System.out.println("Removed player: " + this.receiveThread.getPlayer().getNick());
                //cleans up the game
                if (this.receiveThread.getPlayer().getCurrentGame() != null) {
                    this.receiveThread.getPlayer().getCurrentGame().kickPlayer(this.receiveThread.getPlayer());
                }
                LobbyList.updateLobbies();
                LobbyList.remove_empty_lists();

            }

            else{
                receiveThread.stopThread();
            }

        } catch (IOException e) {
            e.printStackTrace();
            receiveThread.stopThread();
        }
    }

    @Override
    public String toString(){
        return command;
    }
}
