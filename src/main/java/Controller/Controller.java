package Controller;

import Model.Model;
import View.View;

public class Controller {
    private Model model;
    private View view;
    public Controller(Model m, View v) {
        this.model = m;
        this.view = v;
    }
    public Controller(Model m){
        this.model = m;
        this.view = null;
    }
    public void run(){
            //menu things here. split out a ui thread and wait for callbacks.
            // have view dispatch the game loop.
    }
    public void gameLoop(){
        long after;
        long deltaT = 0;
        while(model.isRunning()){
            long now = System.nanoTime();
            model.update(deltaT);
            //try {
            //    Thread.sleep(1);
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
            after = System.nanoTime();
            deltaT = Math.abs(after - now);
        }
    }
}
