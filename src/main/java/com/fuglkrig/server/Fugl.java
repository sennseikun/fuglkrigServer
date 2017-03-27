package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityBird;

import java.awt.*;

/**
 * Created by Tore on 20.03.2017.
 */
public class Fugl extends GameObject implements EntityBird {

    double speedX,speedY;

    public Fugl(double x, double y, double height, double width){
        super(x, y, width, height);
    }


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

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
}