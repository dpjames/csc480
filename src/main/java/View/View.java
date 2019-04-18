package View;

import Model.Model;
import Model.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class View {
    private static final String TITLE = "A very COOL title";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private JFrame window;
    private DrawingBoard canvas;
    private Model model;
    public View(Model m){
        System.out.println("init view");
        this.model = m;
        window = new JFrame(TITLE);
        window.setVisible(true);
        canvas = new DrawingBoard();
        window.setSize(WIDTH, HEIGHT);
        canvas.setSize(WIDTH, HEIGHT);
        window.setLayout(new BorderLayout());
        window.add("Center", canvas);
        //try {
        //    Thread.sleep(2000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        Timer drawTime = new Timer(1, canvas);
        drawTime.start();
    }


    public void render(Model model) {
        //need to generate graphics and such here.
        //chances are this will be replaced with a "draw" or "update" function when this class is extended.
        canvas.repaint();
        for(GameObject o : model.getGameObjects()){
        }
    }


    private class DrawingBoard extends JPanel implements ActionListener{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            System.out.println("update!");
            g.setColor(Color.BLUE);
            g.fillRect(0,0,WIDTH,HEIGHT);
            for(GameObject o : model.getGameObjects()){
                o.render(g);
            }
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            repaint();
        }
    }
}
