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
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
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
    public AIInputHandler(Model m) {
        this.model = m;
        int seed = 123;
        int numInputs = 1;
        int numOutputs = 4;
        int numHiddenNodes = 20;
        double learningRate = .01;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
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
    public void update(ArrayList<Enemy> enemies){

        dmodel.fit(buildState(enemies));
        //evaluate and move here
        int dir = (int)(Math.random() * 4);
        switch(dir){
            case(0):
                movePlayer(VELOCITY * 2, 0);
                break;
            case(1):
                movePlayer(-VELOCITY * 2, 0 );
                break;
            case(2):
                movePlayer(0, VELOCITY * 2);
                break;
            case(3):
                movePlayer(0, -VELOCITY * 2);
                break;
        }

    }

    private DataSet buildState(ArrayList<Enemy> enemies) {
        double[][] pos = new double[enemies.size()][2];
        for(int i = 0; i < enemies.size(); i++){
            double[] cpos = enemies.get(i).getPosition();
            pos[i] = cpos;
        }
        INDArray features = Nd4j.createFromArray(pos);
        INDArray labels = null;
        DataSet alldata = new DataSet(features, labels);
        return alldata;
    }
}
