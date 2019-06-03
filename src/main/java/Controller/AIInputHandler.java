package Controller;
import Model.Model;
import Model.Enemy;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.activations.impl.ActivationCube;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import Model.Constants;
import org.nd4j.linalg.primitives.Pair;
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


    private static final String LEFT = "LEFT";
    @Override
    public void movePlayer(double xv, double yv){
        super.movePlayer(xv,yv);
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
            }
        }
    };
    private static final String WALLS = "WALLS";
    private Action walls = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Constants.WALL_KILL = !Constants.WALL_KILL;
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

        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L,0), WALLS);
        canvas.getActionMap().put(WALLS,walls);

        this.model = m;
        initDmod();
    }
    public void initDmod(){
        int numInputs = 6;//model.getGameObjects().size() * N_VAR_PER - N_VAR_PER + 2;
        int numOutputs = 4;
        int numHiddenNodes = 200;
        double learningRate = .00001;
        for(int i = 0; i < N_PER_GEN; i++) {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .weightInit(WeightInit.XAVIER)
                    .seed(System.nanoTime())
                    .updater(new Nesterovs(learningRate, 0.9))
                    .list()
                    .layer(new DenseLayer.Builder().nIn(numInputs).weightInit(WeightInit.XAVIER).nOut(numHiddenNodes/2)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new DenseLayer.Builder().nIn(numHiddenNodes/2).weightInit(WeightInit.XAVIER).nOut(numHiddenNodes/4)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new DenseLayer.Builder().nIn(numHiddenNodes/4).weightInit(WeightInit.XAVIER).nOut(numHiddenNodes/4)
                            .activation(Activation.RELU)
                            .build())
                    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                            .nIn(numHiddenNodes/4).nOut(numOutputs).build())
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
        float[] dir = new float[4];
        dir[_LEFT] = left;
        dir[_RIGHT] = right;
        dir[_UP] = up;
        dir[_DOWN] = down;
        int max = 0;
        for(int i = 0; i < dir.length; i++){
            if(dir[i] > dir[max]){
                max = i;
            }
        }
        switch(max){
            case _LEFT:
                movePlayer(VELOCITY * -1, 0);
                break;
            case _RIGHT:
                movePlayer(VELOCITY, 0);
                break;
            case _UP:
                movePlayer(0, -1 * VELOCITY);
                break;
            case _DOWN:
                movePlayer(0, VELOCITY);
                break;

        }
        //float x = right - left;
        //float y = down - up;
        //movePlayer(VELOCITY * x, VELOCITY * y);
    }
    private double DISTANCE_THRESHOLD = 100;
    private INDArray makeInput(){
        ArrayList<Enemy> enemies = model.getEnemies();
        double[] ppos = model.getPlayerPosition();
        double[][] clear = new double[1][6];
        for(int i = 0; i < enemies.size(); i++){
            double[] epos = enemies.get(i).getPosition();
            double ewidth = enemies.get(i).getWidth();
            double distance = getDistance(epos, ewidth, ppos);
            if(distance < DISTANCE_THRESHOLD) {
                if (epos[1] > ppos[1]) {
                    clear[0][_DOWN] = 1;
                } else {
                    clear[0][_UP] = 1;
                }
                if (epos[0] > ppos[0]) {
                    clear[0][_LEFT] = 1;
                } else {
                    clear[0][_RIGHT] = 1;
                }
            }
        }
        clear[0][4] = (ppos[0] + ppos[2] / 2)/Constants.WORLD_WIDTH;
        clear[0][5] = (ppos[1] + ppos[2] / 2)/Constants.WORLD_HEIGHT;
        INDArray features = Nd4j.createFromArray(clear);
        return features;
    }

    private double getDistance(double[] epos, double ewidth, double[] ppos) {
        ppos = new double[]{ppos[0] + ppos[2] / 2,
                ppos[1] + ppos[2] / 2,
                ppos[2]};
        epos = new double[]{epos[0] + ewidth / 2,
                epos[1] + ewidth / 2,
                };
        return Math.sqrt(Math.pow(ppos[0] - epos[0],2) + Math.pow(ppos[1] - epos[1],2));
    }

    //private INDArray makeInput() {
    //    ArrayList<Enemy> enemies = model.getEnemies();
    //    double[] ppos = model.getPlayerPosition();
    //    double[][] pos = new double[1][enemies.size() * N_VAR_PER + 2];
    //    for(int i = 0; i < enemies.size() * N_VAR_PER; i+=N_VAR_PER){
    //        double[] epos = enemies.get(i/N_VAR_PER).getPosition();
    //        pos[0][i] =   (epos[0] + enemies.get(i/N_VAR_PER).getWidth()/N_VAR_PER) - ppos[0];
    //        pos[0][i+1] = (epos[1] + enemies.get(i/N_VAR_PER).getWidth()/N_VAR_PER) - ppos[1];
    //        pos[0][i+2] = enemies.get(i/N_VAR_PER).getWidth();
    //        pos[0][i+3] = epos[0];
    //        pos[0][i+4] = epos[1];
    //    }
    //    pos[0][enemies.size() * N_VAR_PER] = ppos[0];
    //    pos[0][enemies.size() * N_VAR_PER + 1] = ppos[1];

    //    double sum = 0;
    //    for(int i = 0; i < enemies.size() * N_VAR_PER + 2; i++){
    //        sum+=Math.pow(pos[0][i],2);
    //    }
    //    double scaler = Math.sqrt(sum);
    //    for(int i = 0; i < enemies.size() * N_VAR_PER + 2; i++){
    //        pos[0][i] = pos[0][i] / scaler ;
    //    }
    //    INDArray features = Nd4j.createFromArray(pos);
    //    return features;
    //}
    public int N_PER_GEN = 100;
    private double N_CHILD_PER_GEN = .75;
    double MUTATE_CHANCE = .1;
    private int N_KEEP_PER_GEN = (int)(N_PER_GEN * .1);

    public void mutate(int gennum){
        ArrayList<MultiLayerNetwork> topNetworks = new ArrayList<>();
        System.out.println("\rGeneration " + gennum + " average: " + getAvgScore());
        for(int i = 0 ; i < scoreList.size(); i++){
            System.out.print(Math.round(scoreList.get(i)*100.0)/100.0+",");
        }
        System.out.println();
        for(int j = 0; j < N_KEEP_PER_GEN; j++){
            int maxIndex = 0;
            for(int i = 0; i < networkList.size(); i++){
                if(scoreList.get(i) > scoreList.get(maxIndex)){
                    maxIndex = i;
                }
            }
            topNetworks.add(networkList.get(maxIndex));
            scoreList.remove(maxIndex);
            networkList.remove(maxIndex);
        }
        scoreList.clear();
        networkList.clear();
        for(MultiLayerNetwork l : topNetworks){
            networkList.add(l);
        }
        int remaining = N_PER_GEN - N_KEEP_PER_GEN;
        for(int i = 0; i < remaining * N_CHILD_PER_GEN; i++){
            MultiLayerNetwork p1 = topNetworks.get((int) Math.round(Math.random() * (N_KEEP_PER_GEN - 1))).clone(); //get a random good network
            MultiLayerNetwork p2 = topNetworks.get((int) Math.round(Math.random() * (N_KEEP_PER_GEN - 1))).clone(); //get a random good network
            Layer[] p1layers = (Layer[]) p1.getLayers();
            Layer[] p2layers = (Layer[]) p2.getLayers();
            //ArrayList<Layer> newLayers = new ArrayList<>();
            Layer[] newLayers = new Layer[p1layers.length];
            for(int j = 0; j < newLayers.length; j++){
                newLayers[j] = (Math.random() > .5 ? p1layers[j] : p2layers[j]);
            }
            MultiLayerNetwork cnet = topNetworks.get((int) Math.round(Math.random() * (N_KEEP_PER_GEN - 1))).clone();
            cnet.setLayers(newLayers);
            networkList.add(cnet);
            remaining--;
        }

        for(int i = 0; i < remaining; i++){
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
                if(Math.random() < MUTATE_CHANCE) {
                    cnet.setParam(key, Nd4j.rand(values.shape()));//set some random values
                }
            }
            networkList.add(cnet);
        }


    }

    private double getAvgScore() {
        double total = 0;
        for(int i  = 0; i < networkList.size(); i++){
            total+=scoreList.get(i);
        }
        return total / networkList.size();
    }

    public void setDmodel(int i) {
        dmodel = networkList.get(i);
    }

    public void addScore(double score) {
        scoreList.add(score);
    }
}
