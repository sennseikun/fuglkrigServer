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

    public void tick() {
        x -= 5;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    //todo fix witdh and height
    public Rectangle getBounds() {
        return super.getBounds();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
