package com.fuglkrig.server.classes;



        import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public interface EntityPowerUp {
    public void tick(int speed);
    public Rectangle getBounds();

    public double getX();
    public double getY();

}
