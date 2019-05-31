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
import org.nd4j.linalg.learning.config.Nesterovs;
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
    private final int N_VAR_PER = 5;

    private ArrayList<INDArray[]> states = new ArrayList<>();
    private static int prevDir = 0;
    private static final String LEFT = "LEFT";
    @Override
    public void movePlayer(double xa, double ya){
        super.movePlayer(xa,ya);
        int dir = 0;
        if(xa > 0){
            dir = _RIGHT;
        } else if(xa < 0){
            dir = _LEFT;
        } else if(ya > 0){
            dir = _DOWN;
        } else if(ya < 0){
            dir = _UP;
        }
        prevDir = dir;
        //addDataPoint();
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
                train(0);
            }
        }
    };
    public double maxScore = 0;
    public double avgScore = 0;
    public double totScore = 0;
    public int ngames = 0;
    void train(double thisScore){
        if(thisScore < .4 * avgScore){
            return;
        }
        ngames++;
        boolean remake = avgScore * 1.4 < thisScore || thisScore > maxScore;
        int iterations = 1;
        if(remake){
            System.out.println("remake model");
            avgScore = thisScore;
            totScore = thisScore * ngames;
        }
        System.out.println(avgScore * .75 + " ... " + thisScore + " .,.. " + ((avgScore * .75) < thisScore));
        if((avgScore * .75  < thisScore) || remake) {
            //initDmod();
            System.out.println("better, training");
            maxScore = maxScore > thisScore ? maxScore : thisScore;
            for (int i = 0; i < iterations; i++) {
                System.out.println(i);
                for (INDArray[] a : states) {
                    dmodel.fit(new DataSet(a[0], a[1]));
                }
            }
        }
        totScore+=thisScore;
        avgScore=totScore/ngames;
        states.clear();
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
        initDmod();
    }
    public void initDmod(){
        int numInputs = model.getGameObjects().size() * N_VAR_PER - N_VAR_PER + 4;
        int numOutputs = N_OUTPUT_MOD * 4;
        int numHiddenNodes = 100;
        double learningRate = .000001;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .seed(System.nanoTime())
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.HARDSIGMOID)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RATIONALTANH)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        dmodel = new MultiLayerNetwork(conf);
        dmodel.init();

    }
    public void addDataPoint(){
        if(Constants.RUN_AI) {
            states.add(makeState(model.getEnemies(), model.getPlayerPosition(), prevDir));
        }
    }
    private float px = 0;
    private float py = 0;
    private float ldist = 0;
    private float TURN_THRESH = .2f;
    public void update(ArrayList<Enemy> enemies, double[] ppos){
        INDArray input = makeInput(enemies, ppos);
        INDArray output = dmodel.output(input);
        //dmodel.fit(buildState(enemies));
        float left = output.getFloat(_LEFT) + output.getFloat(_LEFT * 2) / 2;
        float right = output.getFloat(_RIGHT) + output.getFloat(_RIGHT * 2) / 2;
        float up = output.getFloat(_UP) + output.getFloat(_UP * 2) / 2;
        float down = output.getFloat(_DOWN) + output.getFloat(_DOWN * 2) / 2;
        float x = right - left;
        float y = down - up;

        movePlayer(VELOCITY * x, VELOCITY * y);

        float cdist = getClosestDistance(enemies);
        if(cdist > ldist){
            addDataPoint();
        }
        ldist = cdist;
        //if(Math.abs(x - px) > TURN_THRESH || Math.abs(y - py) > TURN_THRESH){
        //    addDataPoint();
        //}
        px = right - left;
        py = down  - up;
    }

    private float getClosestDistance(ArrayList<Enemy> enemies) {
        double minDist = 100000;
        double[] ppos = model.getPlayerPosition();
        for(Enemy e : enemies){
            double[] epos = e.getPosition();
            double cdist = Math.sqrt(Math.pow(epos[0] - ppos[0] ,2) + Math.pow(epos[1] - ppos[1],2));
            if(cdist < minDist){
                minDist = cdist;
            }
        }
        return (float) minDist;
    }


    private INDArray makeInput(ArrayList<Enemy> enemies, double[] ppos) {
        double[][] pos = new double[1][enemies.size() * N_VAR_PER + 4];
        for(int i = 0; i < enemies.size() * N_VAR_PER; i+=N_VAR_PER){
            double[] epos = enemies.get(i/N_VAR_PER).getPosition();
            pos[0][i] =   (epos[0] + enemies.get(i/N_VAR_PER).getWidth()/N_VAR_PER) - ppos[0];
            pos[0][i+1] = (epos[1] + enemies.get(i/N_VAR_PER).getWidth()/N_VAR_PER) - ppos[1];
            pos[0][i+2] = enemies.get(i/N_VAR_PER).getWidth();
            pos[0][i+3] = epos[0];
            pos[0][i+4] = epos[1];
        }
        pos[0][enemies.size() * N_VAR_PER] = ppos[0];
        pos[0][enemies.size() * N_VAR_PER + 1] = ppos[1];
        pos[0][enemies.size() * N_VAR_PER + 2] = px;
        pos[0][enemies.size() * N_VAR_PER + 3] = py;
        INDArray features = Nd4j.createFromArray(pos);
        return features;
    }
    private int N_OUTPUT_MOD = 2;
    private INDArray[] makeState(ArrayList<Enemy> enemies, double[] ppos, int dir) {
        double[][] pos = new double[1][enemies.size() * N_VAR_PER + 4];
        int[][] lab = new int[1][N_OUTPUT_MOD * 4];
        int index = (int) (Math.round(Math.random() * N_OUTPUT_MOD) * dir);
        lab[0][index] = 1;
        for(int i = 0; i < enemies.size() * N_VAR_PER; i+=N_VAR_PER){
            double[] epos = enemies.get(i/N_VAR_PER).getPosition();
            pos[0][i] =   (epos[0] + enemies.get(i/N_VAR_PER).getWidth()/N_VAR_PER) - ppos[0];
            pos[0][i+1] = (epos[1] + enemies.get(i/N_VAR_PER).getWidth()/N_VAR_PER) - ppos[1];
            pos[0][i+2] = enemies.get(i/N_VAR_PER).getWidth();
            pos[0][i+3] = epos[0];
            pos[0][i+4] = epos[1];
            //pos[0][i+1] = enemies.get(i/N_VAR_PER).getWidth();
            //pos[0][i+2] = epos[0];
            //pos[0][i+3] = epos[1];
        }
        pos[0][enemies.size() * N_VAR_PER] = ppos[0];
        pos[0][enemies.size() * N_VAR_PER + 1] = ppos[1];
        pos[0][enemies.size() * N_VAR_PER + 2] = px;
        pos[0][enemies.size() * N_VAR_PER + 3] = py;
        INDArray features = Nd4j.createFromArray(pos);
        INDArray labels = Nd4j.createFromArray(lab);
        return new INDArray[]{features, labels};
    }
}
