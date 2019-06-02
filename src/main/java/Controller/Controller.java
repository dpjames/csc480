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
        int generationNumber = 0;
        while(true) {
            generationNumber++;
            for(int i = 0; i < inputhandle.N_PER_GEN; i++) {
                System.out.println(generationNumber + "." + i);
                model.reset();
                inputhandle.setDmodel(i);
                while (model.isRunning()) {
                    long now = System.nanoTime();
                    model.update(deltaT);
                    if (!model.isRunning()) {
                        break;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    after = System.nanoTime();
                    deltaT = Math.abs(after - now);
                    //modify timedd
                    deltaT *= Constants.TIME_MOD;
                    if(Constants.RUN_AI){
                        inputhandle.update();
                    }
                }
                inputhandle.addScore(model.getScore());
            }
            if(inputhandle != null){
                inputhandle.mutate();
            }
        }
    }

}
