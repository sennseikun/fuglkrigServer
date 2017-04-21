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

    private int x;
    private int y;
    private int width;
    private int height;

    public GameObject(int x, int y, int width, int height){
        this.width = width;
        this.y = y;
        this.height = height;
        this.x = x;
    }



    /**
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
    public Rectangle getBounds(double scale){
        return new Rectangle(this.getX(),this.getY(),(int)(this.getWidth()*scale),(int)(this.getHeight()*scale));
    }


    public void setHeight(int height){
        this.height = height;
    }

    public void setWidth(int width){
        this.width = width;
    }

    /**
     *
     * @return the x value of a gameObject
     */
    public int getX(){
        return x;
    }

    /**
     *
     * @return the y value of a gameObject
     */
    public int getY(){
        return y;
    }

    /**
     *
     * @return the width of the object
     */
    public int getWidth(){
        return width;
    }

    /**
     *
     * @return the height of the object
     */
    public int getHeight(){
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
