package com.fuglkrig.server;

import java.util.ArrayList;

/**
 * Created by Magnus on 03.03.2017.
 */
public class Map {
    String mapName;
    String currentMap;
    String nextMap;
    String winMap;
    int mapWidth;
    int winMapWidth;
    int mapType;
    int mapXPos;
    int nextMapXPos;


    //maptype is 1, 2 or 3.
    public Map(int mapType) {

        this.mapType = mapType;

        //this sets the map name
        if (mapType == 1) {
            this.mapName = "egypt";
            this.currentMap = "egypt1";
            this.nextMap = "egypt2";
            this.nextMap = "egypt3";
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
            this.mapName = "todo";
        }
        else {
            System.out.println("\n\n\nWARNING: TRIED TO START A MAP THAT DOESNT EXIST, number: " + mapType + "\n\n\n");
        }
    }

    public String getMapName() {
        return this.mapName;
    }

    //moves the map. one map is 1652px wide
    public void moveMap(int speed, boolean lastManStanding) {
        if (lastManStanding){


            //if winningmap got pos 0
            if (nextMapXPos <= 0) {

            }
        }

        //the normal map loop
        else {
            mapXPos -= speed;
            nextMapXPos = mapXPos + mapWidth;

            //hvis mapXPos er "ferdig"
            if (mapXPos <= mapWidth * -1) {
                //mapxpos får nextmap sin posisjon. nextmap får ny posisjon bortenfor
                mapXPos = nextMapXPos;
                nextMapXPos = mapXPos + mapWidth;
            }
        }
    }
}
