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
    private int winPosX;
    private int winPosY;
    private boolean stopmovingscr;


    /**
     * maptype is 1, 2 or 3.
     * @param mapType
     */
    public Map(int mapType) {

        this.mapType = mapType;
        this.mapXPos = 0;

        /**
         * the position you go to when you are the last man standing. this should be the nest
         */
        this.winPosX = 1700;
        this.winPosY = 500;


        /**
         * this sets the map name
         */
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
            this.mapWidth = 1652;
            this.winMapWidth = 550;
        }
        else {
            System.out.println("\n\n\nWARNING: TRIED TO START A MAP THAT DOESNT EXIST, number: " + mapType + "\n\n\n");
        }
        stopmovingscr = false;
    }

    /**
     * @return the name of the maps.
     */
    public String getMapName() {
        return this.mapName;
    }

    /**
     * @return the current map.
     */
    public String getCurrentMap() {
        return this.currentMap;
    }

    /**
     * @return the next map.
     */
    public String getNextMap() {
        return nextMap;
    }

    /**
     * @return the maps used to show the win screen.
     */
    public String getWinMap() {
        return this.winMap;
    }

    /**
     * @return the width of the map
     */
    public int getMapWidth() {
        return this.mapWidth;
    }

    /**
     * @return the width of the winmap
     */
    public int getWinMapWidth() {
        return this.winMapWidth;
    }

    /**
     * @return the map type
     */
    public int getMapType() {
        return this.mapType;
    }

    /**
     * @return the maps x position.
     */
    public int getMapXPos() {
        return this.mapXPos;
    }

    /**
     * @return the next maps x position.
     */
    public int getNextMapXPos() {
        return this.nextMapXPos;
    }

    /**
     * @return the winmaps next x position.
     */
    public int getWinMapXPos() {
        return this.winMapXPos;
    }

    /**
     * @return the win positions x value.
     */
    public int getWinPosX() {
        return this.winPosX;
    }

    /**
     * @return the win positions y walue.
     */
    public int getWinPosY() {
        return this.winPosY;
    }

    /**
     * moves the map. one map is 1652px wide
     * @param speed
     * @param lastManStanding
     * @param gameSizeX
     */
    public void moveMap(int speed, boolean lastManStanding, int gameSizeX) {
        mapXPos -= speed;
        nextMapXPos = mapXPos + mapWidth;
        boolean winMapDone = false;
        if (lastManStanding) {
            if (gameSizeX - winMapXPos <= winMapWidth) {
                System.out.println("moving winscreen");
                winMapXPos -= speed;
            }
            else {
                if (!stopmovingscr) {
                    System.out.println("stopped moving winscreen");
                    stopmovingscr = true;
                }
                winMapDone = true;
            }
        }
        /**
         * hvis mapXPos er "ferdig" og vi winmap ikke er på rett posisjon
         */
        if ((mapXPos <= mapWidth * -1) && !winMapDone) {
            /**
             * mapxpos får nextmap sin posisjon. nextmap får ny posisjon bortenfor
             */
            mapXPos = nextMapXPos;
            nextMapXPos = mapXPos + mapWidth;
        }



    }
}
