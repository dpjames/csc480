package Model;

import java.util.ArrayList;

public class Model {
    private double EPSILON = .00001;
    private boolean gameOpen = true;
    private boolean running = true;
    private ArrayList<GameObject> gameObs;


    public Model(){
        createGameObs();
    }
    private void createGameObs(){
        gameObs = new ArrayList<>();
        gameObs.add(new Player());
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
        System.out.println(seconds);
        for(GameObject o : gameObs){
            o.update(seconds);
        }
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObs;
    }
}
