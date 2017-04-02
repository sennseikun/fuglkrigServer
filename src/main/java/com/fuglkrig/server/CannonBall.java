package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityCanonBall;

import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public class CannonBall extends GameObject implements EntityCanonBall {

    public CannonBall(double x, double y, double height, double width, String defaultLocation){
        super(x, y, width, height, defaultLocation);
    }

    /**
     *  Updateing the movement of the CannonBall
     **/
    public void tick() {
        x += 10;
        /*if(CollisionDetection.collision(this,e)){
            //Todo destroy wall
        }*/
    }

    /**
     *  Using the rectangle size to get collision detect done easier
     **/
    public Rectangle getBounds() {
        return super.getBounds();
    }

    /**
     *  Returns the x coordinate of the object
     **/
    public double getX() {
        return x;
    }

    /**
     *  Returns the y coordinate of the object
     **/
    public double getY() {
        return y;
    }
}
