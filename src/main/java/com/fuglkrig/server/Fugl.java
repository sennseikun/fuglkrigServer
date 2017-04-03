package com.fuglkrig.server;


import com.fuglkrig.server.classes.EntityBird;

import java.awt.*;
import java.io.InputStream;

/**
 * Created by Tore on 20.03.2017.
 * The object fugl, which translates to bird, is going to controll the players bird.
 */
public class Fugl extends GameObject implements EntityBird {

    private int speedX,speedY;
    private InputStream image_is;
    private int id = -1;

    public Fugl(int id,int x, int y, int height, int width){
        super(x, y, width, height);
        this.image_is = image_is;
        this.id = id;
    }


    public InputStream getIS(){
        return image_is;
    }

    @Override
    public String toString(){
        return "Fugl: xPos: " + super.getX() + " yPos: "+ super.getY() + " Height: " + getHeight() +" Width: "+ getWidth();
    }

    /**
     * Update bird function.
     * Checking collision with other birds, walls and powerUps
     */
    public void tick() {
        setX(super.getX() + speedX);
        setY(super.getY() + speedY);
        /*if(CollisionDetection.collisionBird(this,birds)){
            //Todo move the birds
        }
        if(CollisionDetection.collision(this,walls)){
            player.alive=false;
        }
        if(CollisionDetection.collisionPowerup(this, powerUps)){
            //Todo add powerup
        }*/
    }

    /**
     * When you want to check it the object collides with anything.
     * @return the rectangle's bounds.
     */
    public Rectangle getBounds() {
        return super.getBounds();
    }

    /**
     * When you want to know where the bird is on the x axis
     * @return the x position of the bird
     */
    public int getX() {
        return super.getX();
    }

    /**
     * When you want to know where the bird is on the y axis
     * @return the y position of the bird
     */
    public int getY() {
        return super.getY();
    }

    /**
     * When you want to get the speed of the bird in the x axis
     * @return the x speed of the bird
     */
    public double getSpeedX() {
        return speedX;
    }

    /**
     * When you want to get the speed of the bird in the y axis
     * @return the y speed of the bird
     */
    public double getSpeedY() {
        return speedY;
    }

    /**
     * setting the speed of the bird in the x axis
     * @param speedX
     */
    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    /**
     * Setting the speed of the bird in the y axis
     * @param speedY
     */
    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}