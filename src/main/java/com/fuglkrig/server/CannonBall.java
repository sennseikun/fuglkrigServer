package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityCanonBall;

import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public class CannonBall extends GameObject implements EntityCanonBall {

    public CannonBall(int x, int y, int height, int width) {
        super(x, y, width, height);
    }

    /**
     * Updateing the movement of the CannonBall
     **/
    public void tick() {
        setX(super.getX() + 10);
        /*if(CollisionDetection.collision(this,e)){
            //Todo destroy wall
        }*/
    }

    /**
     * Using the rectangle size to get collision detect done easier
     **/
    public Rectangle getBounds() {
        return super.getBounds();
    }

    /**
     * Returns the x coordinate of the object
     **/
    public int getX() {
        return super.getX();
    }

    /**
     * Returns the y coordinate of the object
     **/
    public int getY() {
        return super.getY();
    }
}