package main.java.com.fuglkrig.server;

import main.java.com.fuglkrig.server.classes.EntityWall;

import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public class Wall extends GameObject implements EntityWall {

    public Wall(double x, double y, double height, double width){
        super(x, y, width, height);
    }

    public void tick() {
        x -= 5;
        /*if(CollisionDetection.collision(this,walls)){
            //Todo destroy walls perhaps?
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
}
