package main.java.com.fuglkrig.server;

import main.java.com.fuglkrig.server.classes.EntityCanonBall;

import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public class CannonBall extends GameObject implements EntityCanonBall {

    public CannonBall(double x, double y, double height, double width){
        super(x, y, width, height);
    }

    public void tick() {
        x += 10;
        /*if(CollisionDetection.collision(this,e)){
            //Todo destroy wall
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
