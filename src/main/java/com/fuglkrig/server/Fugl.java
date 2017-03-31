package com.fuglkrig.server;


import com.fuglkrig.server.classes.EntityBird;

import java.awt.*;

/**
 * Created by Tore on 20.03.2017.
 * The object fugl, which translates to bird, is going to controll the players bird.
 */
public class Fugl extends GameObject implements EntityBird {

    double speedX,speedY;

    public Fugl(double x, double y, double height, double width){
        super(x, y, width, height);
    }


    /**
     * Update bird function.
     * Checking collision with other birds, walls and powerUps
     */
    public void tick() {
        x += speedX;
        y += speedY;
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
    public double getX() {
        return x;
    }

    /**
     * When you want to know where the bird is on the y axis
     * @return the y position of the bird
     */
    public double getY() {
        return y;
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
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    /**
     * Setting the speed of the bird in the y axis
     * @param speedY
     */
    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
}