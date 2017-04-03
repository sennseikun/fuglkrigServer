package com.fuglkrig.server.classes;


import java.awt.*;

/**
 * Created by Tore on 24.03.2017.
 */
public interface EntityWall {
    public void tick();
    public Rectangle getBounds();

    public int getX();
    public int getY();

}
