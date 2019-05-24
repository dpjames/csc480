package Model;

import java.awt.*;

public class Player extends GameObject{
    private GameObjectArrayList gameObs;
    private double MAXSPEED = 10;
    private boolean dead;

    public Player(GameObjectArrayList gameObs){
        this.px = 10;
        this.py = 10;
        this.width = 100;
        this.height = 100;
        this.color = Color.YELLOW;
        this.gameObs = gameObs;
    }
    public void hit(){
        this.width-=10;
        this.height-=10;
        if(this.width == 0 || this.height == 0){
            this.dead = true;
        }
    }
    public boolean isDead(){
        return dead;
    }
    @Override
    public void update(double deltaT) {
        super.update(deltaT);
        if(Math.abs(vx) > MAXSPEED){
            vx = vx < 0 ? -MAXSPEED : MAXSPEED;
        }
        if(Math.abs(vy) > MAXSPEED){
            vy = vy < 0 ? -MAXSPEED : MAXSPEED;
        }

        //wrap around when hitting an edge
        if(this.px < -1 * this.width){
            this.px = Constants.WORLD_WIDTH;
        } else if(this.px > Constants.WORLD_WIDTH){
            this.px = -1 * this.width;
        }
        if(this.py < -1 * this.width){
            this.py = Constants.WORLD_HEIGHT;
        } else if(this.py > Constants.WORLD_HEIGHT){
            this.py = -1 * this.height;
        }

        //check if collision
        for(Enemy o : this.gameObs.getEnemies()){
            if(collides(o)){
                //System.out.println("collided!!");
                this.hit();
                Enemy.hit();
                o.regen();
            }
        }
    }
    public void modifyVelocity(double vx, double vy) {
        this.vx += vx;
        this.vy += vy;
    }

    public double getWidth() {
        return this.width;
    }

    public double[] getPosition() {
        return new double[]{px, py};
    }
}
