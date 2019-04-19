package Model;

public class GameObjectArrayList extends java.util.ArrayList<GameObject> {
    private Player player;
    public Player getPlayer(){
        return player;
    }
    public void setPlayer(Player p){
        this.player = p;
        this.add(p);
    }
}
