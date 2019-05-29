package Controller;
import Model.Model;
import Model.Enemy;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import Model.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public class AIInputHandler extends InputHandler{
    private MultiLayerNetwork dmodel;

    private final int    _RIGHT  = 0;
    private final int    _LEFT  = 1;
    private final int    _DOWN  = 2;
    private final int    _UP  = 3;

    private ArrayList<INDArray[]> states = new ArrayList<>();
    private static int prevDir = 0;
    private static final String LEFT = "LEFT";
    @Override
    public void movePlayer(double xa, double ya){
        super.movePlayer(xa,ya);
        int dir = 0;
        if(xa > 0){
            dir = _LEFT;
        } else if(xa < 0){
            dir = _RIGHT;
        } else if(ya > 0){
            dir = _DOWN;
        } else if(ya < 0){
            dir = _UP;
        }
        prevDir = dir;
    }

    private Action left = new AbstractAction(LEFT){
        @Override
        public void actionPerformed(ActionEvent e) {
            movePlayer(-VELOCITY,0);
        }
    };
    private static final String RIGHT = "RIGHT";
    private Action right = new AbstractAction(RIGHT){
        @Override
        public void actionPerformed(ActionEvent e) {
            movePlayer(VELOCITY,0);
        }
    };

    private static final String UP = "UP";
    private Action up = new AbstractAction(UP){
        @Override
        public void actionPerformed(ActionEvent e) {
            movePlayer(0,-VELOCITY);
        }
    };
    private static final String DOWN = "DOWN";
    private Action down = new AbstractAction(DOWN){
        @Override
        public void actionPerformed(ActionEvent e) {
            movePlayer(0,VELOCITY);
        }
    };

    private static final String SPEED_UP = "SPEED_UP";
    private Action speed_up = new AbstractAction(SPEED_UP) {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Constants.TIME_MOD*=2;
        }
    };
    private static final String SPEED_DOWN = "SPEED_DOWN";
    private Action speed_down = new AbstractAction(SPEED_DOWN) {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Constants.TIME_MOD/=2;
        }
    };
    private static final String SWAP = "SWAP";
    private Action swap = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Constants.RUN_AI = !Constants.RUN_AI;
            if(Constants.RUN_AI){
                train();
            }
        }
    };
    private void train(){
        System.out.println("train!!");
        for(int i = 0; i < 500; i++) {
            for (INDArray[] a : states) {
                dmodel.fit(new DataSet(a[0], a[1]));
            }
        }
    }
    public AIInputHandler(JPanel canvas, Model m){
        System.out.println("init action maps");
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0), LEFT);
        canvas.getActionMap().put(LEFT,left);


        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0), RIGHT);
        canvas.getActionMap().put(RIGHT,right);


        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W,0), UP);
        canvas.getActionMap().put(UP,up);

        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0), DOWN);
        canvas.getActionMap().put(DOWN,down);


        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M,0), SPEED_UP);
        canvas.getActionMap().put(SPEED_UP,speed_up);
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N,0), SPEED_DOWN);
        canvas.getActionMap().put(SPEED_DOWN,speed_down);

        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R,0), SWAP);
        canvas.getActionMap().put(SWAP,swap);


        this.model = m;
        int seed = 123;
        int numInputs = m.getGameObjects().size() * 2;
        int numOutputs = 4;
        int numHiddenNodes = 100;
        double learningRate = .01;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                //.layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                //        .activation(Activation.RELU)
                //        .build())
                //.layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                //        .activation(Activation.RELU)
                //        .build())
                //.layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                //        .activation(Activation.RELU)
                //        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        dmodel = new MultiLayerNetwork(conf);
        dmodel.init();

    }
    public AIInputHandler(Model m) {
        this.model = m;
        int seed = 123;
        int numInputs = m.getGameObjects().size() * 2;
        int numOutputs = 4;
        int numHiddenNodes = 100;
        double learningRate = .01;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        dmodel = new MultiLayerNetwork(conf);
        dmodel.init();
    }
    public void addDataPoint(){
        if(!Constants.RUN_AI) {
            System.out.println(prevDir);
            states.add(makeState(model.getEnemies(), model.getPlayerPosition(), prevDir));
        }
    }
    public void update(ArrayList<Enemy> enemies, double[] ppos){
        INDArray input = makeInput(enemies, ppos);
        INDArray output = dmodel.output(input);
        //dmodel.fit(buildState(enemies));
        int max = 0;
        for(int i = 0; i < 4; i++){
            if(output.getFloat(max) < output.getFloat(i) ){
                max = i;
            }
        }
        int dir = max;
        switch(dir){
            case(_RIGHT):
                movePlayer(VELOCITY, 0);
                break;
            case(_LEFT):
                movePlayer(-VELOCITY, 0 );
                break;
            case(_DOWN):
                movePlayer(0, VELOCITY);
                break;
            case(_UP):
                movePlayer(0, -VELOCITY);
                break;
        }
    }
    private INDArray makeInput(ArrayList<Enemy> enemies, double[] ppos) {
        double[][] pos = new double[1][enemies.size() * 2 + 2];
        for(int i = 0; i < enemies.size(); i+=2){
            for(int j = 0 ; j < enemies.size(); j+=2){
                double[] epos = enemies.get(i).getPosition();
                pos[0][j] = epos[0];
                pos[0][j+1] = epos[1];
            }
            pos[0][enemies.size()] = ppos[0];
            pos[0][enemies.size() + 1] = ppos[1];
        }
        INDArray features = Nd4j.createFromArray(pos);
        return features;
    }
    private INDArray[] makeState(ArrayList<Enemy> enemies, double[] ppos, int dir) {
        double[][] pos = new double[1][enemies.size() * 2 + 2];
        int[][] lab = {{0,0,0,0}};
        lab[0][dir] = 1;
        for(int i = 0; i < enemies.size(); i+=2){
            for(int j = 0 ; j < enemies.size(); j+=2){
                double[] epos = enemies.get(i).getPosition();
                pos[0][j] = epos[0];
                pos[0][j+1] = epos[1];
            }
            pos[0][enemies.size()] = ppos[0];
            pos[0][enemies.size() + 1] = ppos[1];
        }
        INDArray features = Nd4j.createFromArray(pos);
        INDArray labels = Nd4j.createFromArray(lab);
        return new INDArray[]{features, labels};
    }
}
