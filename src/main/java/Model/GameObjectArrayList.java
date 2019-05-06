package Model;

import java.awt.*;
import java.util.ArrayList;

public class GameObjectArrayList extends java.util.ArrayList<GameObject> {
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    public Player getPlayer(){
        return player;
    }
    public void setPlayer(Player p){
        this.player = p;
        this.add(p);
    }

    public void generateRandom(int i) {
        for(int j = 0; j < i; j++){
            this.addEnemy(new Enemy());
        }
    }
    public void addEnemy(Enemy e){
        this.enemies.add(e);
        this.add(e);
    }
    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }

}
