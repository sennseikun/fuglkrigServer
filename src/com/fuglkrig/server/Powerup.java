package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityPowerUp;

import java.awt.*;

/**
 * Created by Tore on 20.03.2017.
 */
public class Powerup extends GameObject implements EntityPowerUp {

    public Powerup(double x, double y, double height, double width){
        super(x, y, width, height);

    }

    public void tick() {
        x -= 5;
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
