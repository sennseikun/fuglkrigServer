package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityBird;
import com.fuglkrig.server.classes.EntityCanonBall;
import com.fuglkrig.server.classes.EntityPowerUp;
import com.fuglkrig.server.classes.EntityWall;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Magnus on 03.03.2017.
 */
public class CollisionDetection {

    public static boolean collisionBird(EntityBird b, ArrayList<EntityBird> birds){
        for (EntityBird bird: birds) {
            if(b.getBounds().intersects(bird.getBounds())){
                return checkCollision((GameObject)b, (GameObject)bird);
            }
        }
        return false;
    }
    public static boolean collisionPowerup(EntityBird b, ArrayList<EntityPowerUp> powerUps){
        for (EntityPowerUp powerUp: powerUps) {
            if(b.getBounds().intersects(powerUp.getBounds())){
                return checkCollision((GameObject)b, (GameObject)powerUp);
            }
        }
        return false;
    }
    public static boolean collision(EntityBird bird, ArrayList<EntityWall> walls){
        for (EntityWall wall: walls) {
            if(bird.getBounds().intersects(wall.getBounds())){
                return checkCollision((GameObject)bird, (GameObject)wall);
            }
        }
        return false;
    }
    public static boolean collision(EntityWall w, ArrayList<EntityWall> walls){
        for (EntityWall wall: walls) {
            if(w.getBounds().intersects(wall.getBounds())){
                return checkCollision((GameObject)w, (GameObject)wall);
            }
        }
        return false;
    }
    public static boolean collision(EntityCanonBall canonBall, ArrayList<EntityWall> walls){
        for (EntityWall wall: walls) {
            if(canonBall.getBounds().intersects(wall.getBounds())){
                return checkCollision((GameObject)canonBall, (GameObject)wall);
            }
        }
        return false;
    }

    // Returns true if there is a collision between object a and object b
    public static boolean checkCollision(GameObject a, GameObject b){

        // This meth,od detects to see if the images overlap at all. If they do, collision is possible
        int ax1 = (int)a.getX();
        int ay1 = (int)a.getY();
        int ax2 = ax1 + (int)a.getWidth();
        int ay2 = ay1 + (int)a.getHeight();
        int bx1 = (int)b.getX();
        int by1 = (int)b.getY();
        int bx2 = bx1 + (int)b.getWidth();
        int by2 = by1 + (int)b.getHeight();

        if(by2 < ay1 || ay2 < by1 || bx2 < ax1 || ax2 < bx1)
        {
            return false; // Collision is impossible.
        }
        else // Collision is possible.
        {
            // get the masks for both images
            HashSet<String> maskPlayer1 = getMask(a);
            HashSet<String> maskPlayer2 = getMask(b);

            maskPlayer1.retainAll(maskPlayer2);  // Check to see if any pixels in maskPlayer2 are the same as those in maskPlayer1

            if(maskPlayer1.size() > 0){  // if so, than there exists at least one pixel that is the same in both images, thus
                    /*System.out.println("Collision" + count);//  collision has occurred.
                    count++;*/
                return true;
            }
        }
        return false;
    }

    // returns a HashSet of strings that list all the pixels in an image that aren't transparent
    // the pixels contained in the HashSet follow the guideline of:
    // x,y where x is the absolute x position of the pixel and y is the absolute y position of the pixel
    public static HashSet<String> getMask(GameObject go){

        HashSet<String> mask = new HashSet<String>();
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(go.getDefaultImageLocation()));
        } catch (IOException e) {
            System.out.println("error");
        }
        int pixel, a;
        for(int i = 0; i < image.getWidth(); i++){ // for every (x,y) component in the given box,
            for( int j = 0; j < image.getHeight(); j++){

                pixel = image.getRGB(i, j); // get the RGB value of the pixel
                a= (pixel >> 24) & 0xff;

                if(a != 0){  // if the alpha is not 0, it must be something other than transparent
                    mask.add((go.getX()+i)+","+(go.getY()- j)); // add the absolute x and absolute y coordinates to our set
                }
            }
        }

        return mask;  //return our set

    }


}