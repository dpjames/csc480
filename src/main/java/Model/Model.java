package Model;

import java.util.ArrayList;

public class Model {
    private double score = 0;
    private double EPSILON = .00001;
    private boolean gameOpen = true;
    private boolean running = true;
    private GameObjectArrayList gameObs;


    public Model(){
        createGameObs();
    }
    private void createGameObs(){
        gameObs = new GameObjectArrayList();
        gameObs.setPlayer(new Player(gameObs));
        gameObs.generateRandom(20);
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
        score+=seconds;
        for(GameObject o : gameObs){
            o.update(seconds);
        }
        if(gameObs.getPlayer().isDead()){
            this.running = false;
        }
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObs;
    }

    public void movePlayer(double vx, double vy) {
        this.gameObs.getPlayer().modifyVelocity(vx,vy);
    }
    public double getScore(){
        return score;
    }

    public ArrayList<Enemy> getEnemies() {
        return gameObs.getEnemies();
    }


    public void reset() {
        running = true;
        score = 0;
        Enemy.reset();
        createGameObs();
    }

    public double[] getPlayerPosition() {
        return gameObs.getPlayer().getPosition();
    }
}
