package com.fuglkrig.server;

/**
 * Created by Magnus on 03.03.2017.
 */
public class CollisionDetection {

    /**
     *  Checking if the rectangle of the bird are colliding with the powerup button
     */
    public static boolean playerPowerupCollision(Player p1, Powerup pUp, double scaleP, double scalePUp){
        if(p1.getRectangle(scaleP).intersects(pUp.getBounds(scalePUp)) || p1.getRectangle(scaleP).contains(pUp.getBounds(scalePUp))){

            /*HashSet<String> maskPlayer1 = p1.getMask();
            HashSet<String> maskPlayer2 = pUp.getMask();


            maskPlayer1.retainAll(maskPlayer2);  // Check to see if any pixels in maskPlayer2 are the same as those in maskPlayer1
            System.out.println(maskPlayer1);
            if(maskPlayer1.size() > 0){  // if so, than there exists at least one pixel that is the same in both images, thus
                System.out.println("Bit perfect collision occured");//  collision has occurred.
                return true;
            }*/



            return true;
        }
        return false;
    }

    /**
     * Checking if the rectangle of the 2 powerups crashes.
     * @param p1 powerup1
     * @param p2 powerup2
     * @param scale1 screenscale of powerup1
     * @param scale2 screenscale of powerup2
     * @return boolean value if there is a crash between powerups
     */
    public static boolean powerupPowerupCollision(Powerup p1, Powerup p2, double scale1, double scale2){
        if((p1.getBounds(scale1).intersects(p2.getBounds(scale2)) || p1.getBounds(scale1).contains(p2.getBounds(scale2)))){
            return true;
        }
        return false;
    }
}
