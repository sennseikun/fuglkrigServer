package com.fuglkrig.server;

import java.util.ArrayList;

/**
 * Created by Magnus on 03.03.2017.
 */
public class Map {
    private String mapName;
    private String currentMap;
    private String nextMap;
    private String winMap;
    private int mapWidth;
    private int winMapWidth;
    private int mapType;
    private int mapXPos;
    private int nextMapXPos;
    private int winMapXPos;


    //maptype is 1, 2 or 3.
    public Map(int mapType) {

        this.mapType = mapType;
        this.mapXPos = 0;


        //this sets the map name
        if (mapType == 1) {
            this.mapName = "egypt";
            this.currentMap = "egypt1";
            this.nextMap = "egypt2";
            this.winMap = "egypt3";
            this.mapWidth = 1652;
            //TODO, er dette rett?
            this.winMapWidth = 550;

        }
        else if (mapType == 2) {
            this.mapName = "mountains";
            this.currentMap = "mountains1";
            this.nextMap = "mountains2";
            this.winMap = "mountains3";
            //todo er dette rett?
            this.mapWidth = 1652;
            this.winMapWidth = 550;
        }
        else if (mapType == 3) {
            this.mapName = "forrest";
            this.currentMap = "forrest1";
            this.nextMap = "forrest2";
            this.winMap = "forrest3";
        }
        else {
            System.out.println("\n\n\nWARNING: TRIED TO START A MAP THAT DOESNT EXIST, number: " + mapType + "\n\n\n");
        }
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getCurrentMap() {
        return this.currentMap;
    }

    public String getNextMap() {
        return nextMap;
    }

    public String getWinMap() {
        return this.winMap;
    }

    public int getMapWidth() {
        return this.mapWidth;
    }

    public int getWinMapWidth() {
        return this.winMapWidth;
    }

    public int getMapType() {
        return this.mapType;
    }

    public int getMapXPos() {
        return this.mapXPos;
    }

    public int getNextMapXPos() {
        return this.nextMapXPos;
    }

    public int getWinMapXPos() {
        return this.winMapXPos;
    }

    //moves the map. one map is 1652px wide
    public void moveMap(int speed, boolean lastManStanding, int gameSizeX) {
        mapXPos -= speed;
        nextMapXPos = mapXPos + mapWidth;

        boolean winMapDone = false;

        if (lastManStanding) {
            if (gameSizeX - winMapXPos <= winMapWidth) {
                winMapXPos -= speed;
            }
            else {
                winMapDone = true;
            }

        }

        //hvis mapXPos er "ferdig" og vi winmap ikke er på rett posisjon
        if ((mapXPos <= mapWidth * -1) && !winMapDone) {
            //mapxpos får nextmap sin posisjon. nextmap får ny posisjon bortenfor
            mapXPos = nextMapXPos;
            nextMapXPos = mapXPos + mapWidth;
        }



    }
}
