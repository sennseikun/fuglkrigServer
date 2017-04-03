package com.fuglkrig.server;

import java.awt.*;

/**
 * Created by Magnus on 03.03.2017.
 */
public class GameObject {
    private String nick;
    private int id;
    private int code;
    private String defaultImageLocation;

    private double x;
    private double y;
    private double width;
    private double height;

    public GameObject(double x, double y, double width, double height){
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }



    /**
     * Used for bitmapping on the collisiondetection
     * @return a sting with the location of the fugl_image
     */
    public String getDefaultImageLocation(){
        return defaultImageLocation;
    }

    public void setDefaultImageLocation(String imageLocation){
        this.defaultImageLocation = imageLocation;
    }

    /**
     * Used for collisiondetection
     * @return a rectangle that surrounds the the game object
     */
    public Rectangle getBounds(){
        return new Rectangle((int) getX(), (int) getY(), (int) getWidth(),(int) getHeight());
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
        return height;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
