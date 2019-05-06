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
            this.add(new Enemy());
        }
    }
}
