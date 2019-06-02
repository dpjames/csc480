package Model;

import java.awt.*;

public class Player extends GameObject{
    private GameObjectArrayList gameObs;
    private double MAXSPEED = 10;
    private boolean dead;
    private int lives = 1;
    public Player(GameObjectArrayList gameObs){
        this.width = 10;
        this.height = 10;
        this.px = Constants.WORLD_WIDTH/2 - width/2;
        this.py = Constants.WORLD_HEIGHT/2 - height/2;
        this.color = Color.YELLOW;
        this.gameObs = gameObs;
    }
    public void hit(){
        this.lives--;
        if(this.lives <= 0){
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
           this.px = 0;
           this.px = Constants.WORLD_WIDTH;
           hit();
       } else if(this.px > Constants.WORLD_WIDTH){
           this.px = Constants.WORLD_WIDTH - this.width;
           this.px = -1 * this.width;
           hit();
       }
       if(this.py < -1 * this.width){
           this.py = 0;
           this.py = Constants.WORLD_HEIGHT;
           hit();
       } else if(this.py > Constants.WORLD_HEIGHT){
           this.py = Constants.WORLD_HEIGHT - this.height;
           this.py = -1 * this.height;
           hit();
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
        this.vx = vx;
        this.vy = vy;
    }

    public double getWidth() {
        return this.width;
    }

    public double[] getPosition() {
        return new double[]{px, py, width};
    }
    @Override
    public void render(Graphics g){
        g.setColor(this.color);
        g.fillRect((int)px,(int)py,width,height);
        //g.fillOval((int)(this.px + width/2 - 250/2), (int)(this.py + width/2 -250/2),250,250);

    }
}
