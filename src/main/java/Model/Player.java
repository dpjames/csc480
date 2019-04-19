package Model;

import java.awt.*;

public class Player implements GameObject{
    private static float WORLD_TO_PIXEL_SCALE = 50;
    private double vx, vy, ax, ay, px, py; //velocity, acceleration, position;
    private static int WIDTH = 20;
    private static int HEIGHT = 20;

    public Player(){
        this.ay = 9.8;
        this.ax = -10;
    }

    @Override
    public void update(double deltaT) {
        px+=.5 * (ax * WORLD_TO_PIXEL_SCALE) * deltaT * deltaT + (vx * WORLD_TO_PIXEL_SCALE) * deltaT;
        py+=.5 * (ay * WORLD_TO_PIXEL_SCALE) * deltaT * deltaT + (vy * WORLD_TO_PIXEL_SCALE) * deltaT;
        vx+= deltaT * ax;
        vy+= deltaT * ay;

        if(py + HEIGHT > Constants.WORLD_HEIGHT || py < 0){
            //py = 0;
            vy*=-1;
        }
        if(px + WIDTH > Constants.WORLD_WIDTH || px < 0){
            vx*=-1;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect((int)px,(int)py,WIDTH,HEIGHT);
    }

    public void modifyVelocity(double vx, double vy) {
        this.vx += vx;
        this.vy += vy;
    }
}
