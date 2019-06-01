package Controller;
import Model.Model;
import Model.Enemy;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
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
import org.tensorflow.op.core.Mul;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public class AIInputHandler extends InputHandler{
    private MultiLayerNetwork dmodel;
    private ArrayList<MultiLayerNetwork> networkList = new ArrayList<>();
    private ArrayList<Double> scoreList = new ArrayList<>();
    private final int    _RIGHT  = 0;
    private final int    _LEFT  = 1;
    private final int    _DOWN  = 2;
    private final int    _UP  = 3;
    private final int N_VAR_PER = 5;

    private ArrayList<INDArray[]> states = new ArrayList<>();
    private static int prevDir = 0;
    private static final String LEFT = "LEFT";

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
            }
        }
    };
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
        for(int i = 0; i < N_PER_GEN; i++) {
            int numInputs = model.getGameObjects().size() * N_VAR_PER - N_VAR_PER + 2;
            int numOutputs = N_OUTPUT_MOD * 4;
            int numHiddenNodes = 40;
            double learningRate = .001;
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .weightInit(WeightInit.XAVIER)
                    .seed(System.nanoTime())
                    .updater(new Nesterovs(learningRate, 0.9))
                    .list()
                    .layer(new DenseLayer.Builder().nIn(numInputs).weightInit(WeightInit.XAVIER).nOut(numHiddenNodes)
                            .activation(Activation.HARDSIGMOID)
                            .build())
                    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                            .nIn(numHiddenNodes).nOut(numOutputs).build())
                    .build();
            dmodel = new MultiLayerNetwork(conf);
            dmodel.init();
            networkList.add(dmodel);
        }
        System.out.println("init dmod");
    }
    public void update(){
        INDArray input = makeInput();
        INDArray output = dmodel.output(input);
        //dmodel.fit(buildState(enemies));
        float left = output.getFloat(_LEFT);
        float right = output.getFloat(_RIGHT);
        float up = output.getFloat(_UP);
        float down = output.getFloat(_DOWN);
        float x = right - left;
        float y = down - up;
        movePlayer(VELOCITY * x, VELOCITY * y);
    }

    private INDArray makeInput() {
        ArrayList<Enemy> enemies = model.getEnemies();
        double[] ppos = model.getPlayerPosition();
        double[][] pos = new double[1][enemies.size() * N_VAR_PER + 2];
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
        INDArray features = Nd4j.createFromArray(pos);
        return features;
    }
    private int N_OUTPUT_MOD = 1;
    int N_PER_GEN = 10;
    private int N_KEEP_PER_GEN = (int)(N_PER_GEN * .25);
    public void mutate(){
        ArrayList<MultiLayerNetwork> topNetworks = new ArrayList<>();
        for(int j = 0; j < N_KEEP_PER_GEN; j++){
            double maxScore = 0;
            int maxIndex = 0;
            for(int i = 0; i < networkList.size(); i++){
                if(scoreList.get(i) > scoreList.get(maxIndex)){
                    maxScore = scoreList.get(i);
                    maxIndex = i;
                }
            }
            topNetworks.add(networkList.get(maxIndex));
            scoreList.remove(maxIndex);
            networkList.remove(maxIndex);
        }
        System.out.println(topNetworks.size());
        scoreList.clear();
        networkList.clear();
        for(int i = 0; i < N_PER_GEN; i++){
            MultiLayerNetwork cnet = topNetworks.get((int) Math.round(Math.random() * (N_KEEP_PER_GEN - 1))).clone();
            Map<String, INDArray> paramTable = cnet.paramTable();
            Set<String> keys = paramTable.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String key = it.next();
                INDArray values = paramTable.get(key);
                //System.out.print(key+" ");//print keys
                //System.out.println(Arrays.toString(values.shape()));//print shape of INDArray
                //System.out.println(values);
                if(Math.random() < .01) {
                    cnet.setParam(key, Nd4j.rand(values.shape()));//set some random values
                }
            }
            networkList.add(cnet);
        }


    }

    public void setDmodel(int i) {
        dmodel = networkList.get(i);
    }

    public void addScore(double score) {
        scoreList.add(score);
    }
}
