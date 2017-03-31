package com.fuglkrig.server;

import javafx.scene.shape.Circle;

import java.awt.*;

/**
 * Created by Magnus on 03.03.2017.
 */
public class GameObject {
    String nick;
    int id;
    int code;
    String defaultImageLocation;

    double x, y, width, heigth;

    public GameObject(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigth = heigth;
    }

    /**
     *
     * @return the x value of a gameObject
     */
    public double getX(){
        return x;
    }

    /**
     *
     * @return the y value of a gameObject
     */
    public double getY(){
        return y;
    }

    /**
     *
     * @return the width of the object
     */
    public double getWidth(){
        return width;
    }

    /**
     *
     * @return the height of the object
     */
    public double getHeight(){
        return heigth;
    }

    /**
     * Used for bitmapping on the collisiondetection
     * @return a sting with the location of the image
     */
    public String getDefaultImageLocation(){
        return defaultImageLocation;
    }

    /**
     * Used for collisiondetection
     * @return a rectangle that surrounds the the game object
     */
    public Rectangle getBounds(){
        return new Rectangle((int)x, (int)y, (int)width ,(int)heigth);
    }

}
