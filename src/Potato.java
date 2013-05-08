import org.newdawn.slick.*;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * @author Jalict <larsen.frans@gmail.com>
 */
public class Potato extends Rectangle {
	public Image topImage;
    public Image bottomImage;

    public Potato(float x, float y, float width, float height, Image top, Image bottom) throws SlickException {
        super(x, y, width, height);
        this.topImage = top;
        this.bottomImage = bottom;
    }

    public int getScore() throws SlickException {
        int s = getTransparentPercentage();
        System.out.println(s + "%");
//        if(s < 10) {              OLD CODE WHICH DOES NOT WORK PROPERLY ON THE WEB APPLET
//            return -1000;         THIS CODE IS ONLY USED IN THE NATIVE BUILDS OR WHATEVER YOU CALL EM
//        } else if(s < 10) {
//            return -200;
//        } else if(s < 20) {
//            return -100;
//        } else if(s < 45) {
//            return -50;
//        } else if(s >= 45) {
//            return 0;
//        } else if(s >= 70) {
//            return 100+s;
//        } else if(s > 90) {
//            return 200+(s+(50*(s/10)));
//        }
        if(s < 65) {
            return -1000;
        } else if(s < 75) {
            return -200;
        } else if(s < 80) {
            return -100;
        }
        if(s > 95) {
            return 200+(s+(50*(s/10)));
        } else if(s > 90) {
            return 100;
        } else if(s >= 80) {
            return 25;
        }
        return 0;
    }

    public int getTransparentPercentage() throws SlickException {
        int total = 0;
        int trans = 0;

        byte[] top = topImage.getTexture().getTextureData();
        byte[] bottom = bottomImage.getTexture().getTextureData();

        for(int x = 3; x < top.length; x += 4) {

            if(bottom[x]!= 0) {
                total += 1;
                if(top[x] == 0) {
                    trans += 1;
                }
            }
        }
        return ((trans*100)/total);

    }
}
