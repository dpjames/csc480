package Model;

import java.awt.*;

public class GameObjectArrayList extends java.util.ArrayList<GameObject> {
    private Player player;
    public Player getPlayer(){
        return player;
    }
    public void setPlayer(Player p){
        this.player = p;
        this.add(p);
    }

    public void generateRandom(int i) {
        for(int j = 0; j < i; j++){
            this.add(new GameObject() {
                @Override
                public void update(double deltaT){
                    super.update(deltaT);
                    if(this.py + this.height > Constants.WORLD_HEIGHT){
                        this.vy*=-1;
                    }
                    if(this.px < 0){
                       this.px = Math.random() * Constants.WORLD_WIDTH + Constants.WORLD_WIDTH;
                    }
                    if(this.vx < -20){
                       this.vx = Math.random() * 40 - 20;
                    }
                }
            });
        }
    }
}
