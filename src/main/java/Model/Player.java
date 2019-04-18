package Model;

import java.awt.*;

public class Player implements GameObject{
    private double vx, vy, ax, ay, px, py; //velocity, acceleration, position;
    private static int WIDTH = 20;
    private static int HEIGHT = 20;

    public Player(){
        this.vx = 100;
        this.vy = 100;
    }

    @Override
    public void update(double deltaT) {
        px+=.5 * ax * deltaT * deltaT + vx * deltaT;
        py+=.5 * ay * deltaT * deltaT + vy * deltaT;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect((int)px,(int)py,WIDTH,HEIGHT);
    }
}
