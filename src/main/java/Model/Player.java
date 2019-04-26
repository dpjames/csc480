package Model;

import java.awt.*;

public class Player extends GameObject{
    public Player(){
        this.ay = 9.8;
        this.ax = -10;
        this.px = 10;
        this.py = 10;
        this.color = Color.YELLOW;
    }
    @Override
    public void update(double deltaT) {
        super.update(deltaT);
        if(py + height > Constants.WORLD_HEIGHT){
            py = Constants.WORLD_HEIGHT - height;
            vy*=-1;
        }
        if(py < 0) {
            py = 0;
            vy*=-1;
        }
        if(px + width > Constants.WORLD_WIDTH || px < 0){
            vx*=-1;
        }
    }
    public void modifyVelocity(double vx, double vy) {
        this.vx += vx;
        this.vy += vy;
    }
}
