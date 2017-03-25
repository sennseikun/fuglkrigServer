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

    double x, y, width, heigth;

    public GameObject(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigth = heigth;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return heigth;
    }

    //TODO get img filepath for the object
    public String getDefaultImageLocation(){

        return "";
    }

    public Rectangle getBounds(){
        return new Rectangle((int)x, (int)y, (int)width ,(int)heigth);
    }

}
