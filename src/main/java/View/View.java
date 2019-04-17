package View;

import Model.Model;
import Model.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class View {
    private static final String TITLE = "A very COOL title";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private JFrame window;
    private Canvas canvas;
    public View(){
        System.out.println("init view");
        window = new JFrame(TITLE);
        window.setVisible(true);
        canvas = new DrawingBoard();
        window.setSize(WIDTH, HEIGHT);
        canvas.setSize(WIDTH, HEIGHT);
        window.add(canvas);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Graphics g = canvas.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(0,0,WIDTH,HEIGHT);
    }
    public void render(Model model) {
        //need to generate graphics and such here.
        //chances are this will be replaced with a "draw" or "update" function when this class is extended.
        canvas.repaint();
        for(GameObject o : model.getGameObjects()){
            o.render();
        }
    }


    private class DrawingBoard extends Canvas{
        @Override
        public void paint(Graphics g){
            System.out.println("update!");
            g.setColor(Color.BLUE);
            g.fillRect(0,0,WIDTH,HEIGHT);
        }
    }
}
