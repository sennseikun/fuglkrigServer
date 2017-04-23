package com.fuglkrig.server.interfaces;



        import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public interface EntityPowerUp {
    public void tick();
    public Rectangle getBounds(double scale);

    public int getX();
    public int getY();

}
