package com.fuglkrig.server;


import java.awt.*;
import com.fuglkrig.server.classes.EntityPowerUp;

/**
 * Created by Tore on 20.03.2017.
 */
public class Powerup extends GameObject implements EntityPowerUp {

    int id;
    String powerUpName;

    public Powerup(double x, double y, double height, double width,int id){
        super(x, y, width, height);
        this.id = id;


        //TODO fix this to give powers!
        switch (this.id) {
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
        }
    }

    /**
     * update function for the powerup
     */
    public void tick() {
        x -= 5;
    }

    /**
     * Setting the id of the powerup
     * @param id
     */
    public void setId(int id) {
        this.id = id;
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
