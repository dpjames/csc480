package Model;

import java.awt.*;

public class GameObject {
    protected static float WORLD_TO_PIXEL_SCALE = 50;
    protected double vx = 0;
    protected double vy = 0;
    protected double ax = 0;
    protected double ay = 0;
    protected double px = 0;
    protected double py = 0; //velocity, acceleration, position;
    protected int width = 10;
    protected int height = 10;
    protected Color color = Color.RED;
    public GameObject(){
        px = Math.random() * Constants.WORLD_WIDTH + Constants.WORLD_WIDTH;
        py = Math.random() * Constants.WORLD_HEIGHT - height;
    }
    public void update(double deltaT){
        px+=.5 * (ax * WORLD_TO_PIXEL_SCALE) * deltaT * deltaT + (vx * WORLD_TO_PIXEL_SCALE) * deltaT;
        py+=.5 * (ay * WORLD_TO_PIXEL_SCALE) * deltaT * deltaT + (vy * WORLD_TO_PIXEL_SCALE) * deltaT;
        vx+= deltaT * ax;
        vy+= deltaT * ay;
    }
    public void render(Graphics g){
        g.setColor(this.color);
        g.fillRect((int)px,(int)py,width,height);
    }
    public boolean collides(GameObject other){
        double[][] me = {
                {this.px, this.py},
                {this.px + this.width, this.py + this.height}
        };
        double[][] them = {
                {other.px, other.py},
                {other.px + other.width, other.py + other.height}
        };
        if(me[0][0] > them[1][0] || them[0][0] > me[1][0]){
            return false;
        }
        if(me[0][1] > them[1][1] || them[0][1] > me[1][1]){
            return false;
        }
        return true;
    }
    boolean checkInBounds(){
        return (this.px > 0 && this.px + this.width < Constants.WORLD_WIDTH) && (this.py > 0 && this.py + width < Constants.WORLD_HEIGHT);
    }


}
