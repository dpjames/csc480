package Model;

import java.awt.*;

public class GameObject {
    protected static float WORLD_TO_PIXEL_SCALE = 50;
    protected double vx = 0;
    protected double vy = 0;
    protected double ax = -10;
    protected double ay = 9.8;
    protected double px = 0;
    protected double py = 0; //velocity, acceleration, position;
    protected int width = 100;
    protected int height = 100;
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
}
