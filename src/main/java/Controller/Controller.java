package Controller;

import Model.Model;
import View.View;
import Model.Constants;
public class Controller {
    private Model model;
    private View view;
    private AIInputHandler inputhandle;
    public Controller(Model m, View v) {
        this.model = m;
        this.view = v;
    }
    public Controller(Model m){
        this.model = m;
        this.view = null;
    }

    public Controller(Model m, View v, AIInputHandler aiInputHandler) {
        model = m;
        view = v;
        inputhandle = aiInputHandler;
    }

    public void run(){
            //menu things here. split out a ui thread and wait for callbacks.
            // have view dispatch the game loop.
    }
    public void gameLoop(){
        long after;
        long deltaT = 0;
        while(true) {
            model.reset();
            while (model.isRunning()) {
                long now = System.nanoTime();
                model.update(deltaT);
                if(!model.isRunning()){
                    break;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }https://stackoverflow.com/questions/17865465/how-do-i-draw-an-image-to-a-jpanel-or-jframe
                after = System.nanoTime();
                deltaT = Math.abs(after - now);
                //modify time
                deltaT*= Constants.TIME_MOD;
                if (inputhandle != null && Constants.RUN_AI) {
                    inputhandle.update(model.getEnemies(), model.getPlayerPosition());
                }
                if(inputhandle != null && !Constants.RUN_AI){
                    inputhandle.addDataPoint();
                }
            }
            System.out.println(model.getScore());
        }
    }

}
