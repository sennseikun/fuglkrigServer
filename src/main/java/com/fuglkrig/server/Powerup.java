package com.fuglkrig.server;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fuglkrig.server.classes.EntityPowerUp;

import javax.imageio.ImageIO;

/**
 * Created by Tore on 20.03.2017.
 */
public class Powerup extends GameObject implements EntityPowerUp {

    int type;
    String powerUpName;

    BufferedImage img = null;

    public Powerup(int x, int y, int height, int width, int type, BufferedImage img){
        super(x, y, width, height);
        this.type = type;
        this.img = img;


        //TODO fix this to give powers!
        //remember to fix Powerup! It has a randint that needs to be changed
        switch (this.type) {
            case 1: powerUpName = "Wall Forwards";
                break;
            case 2: powerUpName = "Wall Up";
                break;
            case 3: powerUpName = "Wall Down";
                break;
            case 4: powerUpName = "Wall Back";
                break;
            case 5: powerUpName = "Cannonball";
                break;
            case 6: powerUpName = "SpeedUp";
                break;
            case 7: powerUpName = "SpeedDown";
                break;
            case 8: powerUpName = "Fake powerup";
                break;
            default:
                System.out.println("\n\n\nWARNING!\nThe randint in Powerup doesnt match the cases in the other Powerup constructor in the powerup file.\n\n\n ");
        }
    }

    /**
     * update function for the powerup
     */
    public void tick(int speed) {
        x -= speed;
    }

    /**
     * Setting the id of the powerup
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the id of the powerup
     */
    public int getId() {
        return id;
    }

    /**
     * Creates an rectangle around the powerup
     * @return the rectangle
     */
    public Rectangle getBounds() {
        return super.getBounds();
    }

    /**
     * @return the powerups x value
     */
    public double getX() {
        return x;
    }

    /**
     * @return the powerups y value
     */
    public double getY() {
        return y;
    }
}
