package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityWall;

import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public class Wall extends GameObject implements EntityWall {

    /**
     * Creating a wall
     * @param x
     * @param y
     * @param height
     * @param width
     */
    public Wall(int x, int y, int height, int width){
        super(x, y, width, height);
    }

    /**
     * Update function for the movment of the wall
     */
    public void tick() {
        setX(super.getX() - 5);
        /*if(CollisionDetection.collision(this,walls)){
            //Todo destroy walls perhaps?
        }*/
    }

    /**
     * Creates a rectangle around the wall
     * @return the rectangle
     */
    public Rectangle getBounds() {
        return super.getBounds();
    }

    /**
     * @return the x value of the wall
     */
    public int getX() {
        return super.getX();
    }

    /**
     * @return the y value of the wall
     */
    public int getY() {
        return super.getY();
    }
}
