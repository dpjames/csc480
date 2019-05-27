package Controller;
import Model.Model;
import Model.Enemy;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.iter.INDArrayIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.NDArrayFactory;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

public class AIInputHandler extends InputHandler{
    private MultiLayerNetwork dmodel;

    public static double WORLD_HEIGHT = 1000;
    public static double WORLD_WIDTH  = 1000;
    public final int    RIGHT  = 0;
    public final int    LEFT  = 1;
    public final int    DOWN  = 2;
    public final int    UP  = 3;

    public AIInputHandler(Model m) {
        this.model = m;
        int seed = 123;
        int numInputs = m.getGameObjects().size() * 2;
        int numOutputs = 4;
        int numHiddenNodes = 1000;
        double learningRate = .01;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                //.updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        dmodel = new MultiLayerNetwork(conf);
        dmodel.init();
        dmodel.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates
        //dmodel.fit();

        System.out.println("Evaluate model....");
        //Evaluation eval = new Evaluation(numOutputs);
        //while(testIter.hasNext()){
        //    DataSet t = testIter.next();
        //    INDArray features = t.getFeatures();
        //    INDArray lables = t.getLabels();
        //    INDArray predicted = model.output(features,false);

        //    eval.eval(lables, predicted);

        //}

    }
    public void update(ArrayList<Enemy> enemies, double[] ppos){
        //int row = (int)(Math.random() * enemies.size());

        INDArray output = (dmodel.output(buildInput(enemies, ppos)[0]));
        dmodel.fit(buildState(enemies));
        //evaluate and move here
        int max = 0;
        double[] avg = {0,0,0,0};
        for(int i = 0; i < enemies.size(); i++) {
            double a,b,c,d;
            a =output.getRow(0).getDouble(0);
            b =output.getRow(0).getDouble(1);
            c =output.getRow(0).getDouble(2);
            d =output.getRow(0).getDouble(3);
            if(Double.isNaN(a)){
                a = output.getRow(0).getInt(0);
                b = output.getRow(0).getInt(1);
                c = output.getRow(0).getInt(2);
                d = output.getRow(0).getInt(3);
            }
            avg[0]+=a;
            avg[1]+=b;
            avg[2]+=c;
            avg[3]+=d;
        }
        avg[0]/=enemies.size();
        avg[1]/=enemies.size();
        avg[2]/=enemies.size();
        avg[3]/=enemies.size();
        for(int i = 0; i < 4; i++) {
            if (avg[i] > avg[max]){
                max = i;
            }
        }

        //System.out.print(output.getRow(row).getDouble(0));
        //System.out.print(" " + output.getRow(row).getDouble(1));
        //System.out.print(" " + output.getRow(row).getDouble(2));
        //System.out.print(" " + output.getRow(row).getDouble(3));
        //System.out.println();
        int dir = max;
        switch(dir){
            case(RIGHT):
                movePlayer(VELOCITY * 2, 0);
                break;
            case(LEFT):
                movePlayer(-VELOCITY * 2, 0 );
                break;
            case(DOWN):
                movePlayer(0, VELOCITY * 2);
                break;
            case(UP):
                movePlayer(0, -VELOCITY * 2);
                break;
        }

    }

    private INDArray[] buildInput(ArrayList<Enemy> enemies, double[] ppos) {
        int MOD_SIZE = 50;
        double[][] pos = new double[MOD_SIZE][enemies.size() * 2 + 2];
        int[][] lab = new int[MOD_SIZE][4];
        for(int j = 0; j < MOD_SIZE; j++) {
            ppos[0]+=j*Math.random();
            ppos[1]+=j*Math.random();
            for (int i = 0; i < enemies.size(); i += 2) {
                double[] cpos = enemies.get(i).getPosition();
                pos[j][i + 0] = cpos[0];
                pos[j][i + 1] = cpos[1];
                //int[] label = {0,0,0,0};
                //label[(int)(Math.random() * 4)] = 1; //change this
            }
            lab[j][0] = 0;
            lab[j][1] = 0;
            lab[j][2] = 0;
            lab[j][3] = 0;
            pos[j][enemies.size()] = ppos[0];
            pos[j][enemies.size()] = ppos[1];

            double cdist = 1000000; //just a big number //values
            double awayx = 0, awayy = 0;
            for (int i = 0; i < enemies.size(); i++) {
                double x = Math.abs(ppos[0] - enemies.get(i).getPosition()[0]);
                int xdir = 1, ydir = 1;
                if (x / WORLD_WIDTH > 0.5) { //if the x distance calculated is more than half the world width, the other way is shorter
                    x = WORLD_WIDTH - x;
                    xdir = -1;
                }
                double y = Math.abs(ppos[1] - enemies.get(i).getPosition()[1]);
                if (y / WORLD_HEIGHT > 0.5) {
                    y = WORLD_HEIGHT - y;
                    ydir = -1;
                }
                double curDist = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                if (cdist > curDist) {
                    cdist = curDist;
                    awayx = xdir * (ppos[0] - enemies.get(i).getPosition()[0]);  //vector to move away from the closest enemy
                    awayy = ydir * (ppos[1] - enemies.get(i).getPosition()[1]);
                }
            }

            int index = 0;
            if (Math.abs(awayx) > Math.abs(awayy)) {
                index = awayx > 0 ? 0 : 1;
            } else {
                index = awayy > 0 ? 2 : 3;
            }
            lab[j][index] = 1;
        }

        INDArray features = Nd4j.createFromArray(pos);
        INDArray labels = Nd4j.createFromArray(lab);
        return new INDArray[]{features,labels};
    }

    private DataSet buildState(ArrayList<Enemy> enemies) {
        INDArray[] data = buildInput(enemies, model.getPlayerPosition());
        DataSet alldata = new DataSet(data[0], data[1]);
        return alldata;
    }

    public void evaluate() {
        double score = model.getScore();
    }
}
