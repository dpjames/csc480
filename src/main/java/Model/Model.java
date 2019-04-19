package Model;

import java.util.ArrayList;

public class Model {
    private double EPSILON = .00001;
    private boolean gameOpen = true;
    private boolean running = true;
    private GameObjectArrayList gameObs;


    public Model(){
        createGameObs();
    }
    private void createGameObs(){
        gameObs = new GameObjectArrayList();
        gameObs.setPlayer(new Player());
        gameObs.generateRandom(5);
    }

    public boolean gameIsOpen() {
        return gameOpen;
    }
    public void closeGame(){
        gameOpen = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void update(long deltaT) {
        double seconds = deltaT/1000000000.0;
        for(GameObject o : gameObs){
            o.update(seconds);
        }
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObs;
    }

    public void movePlayer(double vx, double vy) {
        this.gameObs.getPlayer().modifyVelocity(vx,vy);
    }
}
