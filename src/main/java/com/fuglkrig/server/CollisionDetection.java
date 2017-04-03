package com.fuglkrig.server;

import com.fuglkrig.server.classes.EntityBird;
import com.fuglkrig.server.classes.EntityCanonBall;
import com.fuglkrig.server.classes.EntityPowerUp;
import com.fuglkrig.server.classes.EntityWall;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Magnus on 03.03.2017.
 */
public class CollisionDetection {

    public static boolean dummyCollides(GameObject g1, GameObject g2) {
        if (g1.getBounds().intersects(g2.getBounds())) {
            return true;
        }
        return false;
    }

    /**
     *  Checking if the rectangle of the birds are colliding and calling pixel perfect collision detection
     */

    public static boolean PlayerCollision(Player p1, Player p2, double scale){
        /*System.out.println("Player1 X: " + p1.getCoordX());
        System.out.println("Player1 Y: " + p1.getCoordY());
        System.out.println("Player1 Width: " + p1.getWidth());
        System.out.println("Player1 Height: " + p1.getHeight());
        System.out.println("Player2 X: " + p2.getCoordX());
        System.out.println("Player2 Y: " + p2.getCoordY());
        System.out.println("Player2 Width: " + p2.getWidth());
        System.out.println("Player2 Height: " + p2.getHeight());*/



        return p1.getRectangle(scale).intersects(p2.getRectangle(scale)) || p1.getRectangle(scale).contains(p2.getRectangle(scale));
    }

    /**
     *  Checking if the rectangle of the birds are colliding and calling pixel perfect collision detection
     */
    public static boolean collisionBird(EntityBird b, ArrayList<EntityBird> birds){
        for (EntityBird bird: birds) {
            System.out.println("gets here #colliosion");
            if(b.getBounds().intersects(bird.getBounds())){
                return true;
                //Todo move the birds
                //return checkCollision((GameObject)b, (GameObject)bird);
            }
        }
        return false;
    }

    /**
     *  Checking if the rectangle of a bird collides with the rectangle of a powerUp and calling pixel perfect collision detection
     */
    public static boolean collisionPowerup(EntityBird b, ArrayList<EntityPowerUp> powerUps){
        for (EntityPowerUp powerUp: powerUps) {
            if(b.getBounds().intersects(powerUp.getBounds())){
                return checkCollision((GameObject)b, (GameObject)powerUp);
            }
        }
        return false;
    }

    /**
     *  Checking if the rectangle of a bird collides with the rectangle of a wall and calling pixel perfect collision detection
     *  #deadBird
     */
    public static boolean collision(EntityBird bird, ArrayList<EntityWall> walls){
        for (EntityWall wall: walls) {
            if(bird.getBounds().intersects(wall.getBounds())){
                return checkCollision((GameObject)bird, (GameObject)wall);
            }
        }
        return false;
    }

    /**
     *  Checking if the rectangle of 2 walls collides and calling pixel perfect collision detection
     */
    public static boolean collision(EntityWall w, ArrayList<EntityWall> walls){
        for (EntityWall wall: walls) {
            if(w.getBounds().intersects(wall.getBounds())){
                return checkCollision((GameObject)w, (GameObject)wall);
            }
        }
        return false;
    }

    /**
     *  Checking if the rectangle of a cannonBall collides with the rectangle of a wall and calling pixel perfect collision detection
     */
    public static boolean collision(EntityCanonBall canonBall, ArrayList<EntityWall> walls){
        for (EntityWall wall: walls) {
            if(canonBall.getBounds().intersects(wall.getBounds())){
                return checkCollision((GameObject)canonBall, (GameObject)wall);
            }
        }
        return false;
    }

    /**
     *  Returns true if there is a collision between object a and object b
     */
    public static boolean checkCollision(GameObject a, GameObject b){

        /**
         *  This method detects to see if the images overlap at all. If they do, collision is possible
         *  todo check if this is usless with the other classes
         */
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
            /**
             * get the masks for both images
             */
            HashSet<String> maskPlayer1 = getMask(a);
            HashSet<String> maskPlayer2 = getMask(b);

            maskPlayer1.retainAll(maskPlayer2);  // Check to see if any pixels in maskPlayer2 are the same as those in maskPlayer1

            if(maskPlayer1.size() > 0){  // if so, than there exists at least one pixel that is the same in both images, thus
                    /**
                     * System.out.println("Collision" + count);//  collision has occurred.
                     * count++;
                     */
                return true;
            }
        }
        return false;
    }

    /**
     * returns a HashSet of strings that list all the pixels in an fugl_image that aren't transparent
     * the pixels contained in the HashSet follow the guideline of:
     * x,y where x is the absolute x position of the pixel and y is the absolute y position of the pixel
     */

    public static HashSet<String> getMask(GameObject go){

        HashSet<String> mask = new HashSet<String>();
        BufferedImage image = null;
        try {
            InputStream is = go.getClass().getClassLoader().getResourceAsStream(go.getDefaultImageLocation());
            image = ImageIO.read(is);
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
        } catch (IOException e) {
            System.out.println("error");
        }
        //System.out.println(mask);

        return mask;  //return our set

    }
}
