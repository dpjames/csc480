package View;

import Controller.GUIInputHandler;
import Model.Model;
import Model.GameObject;
import Model.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class View {
    private static final String TITLE = "A very COOL title";
    private JFrame window;
    private DrawingBoard canvas;
    private Model model;
    private GUIInputHandler inputHandler;
    public View(Model m){
        System.out.println("init view");
        this.model = m;
        window = new JFrame(TITLE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        canvas = new DrawingBoard();

        //canvas.setSize((int) Constants.WORLD_WIDTH, (int) Constants.WORLD_HEIGHT);

        window.setLayout(new BorderLayout());
        window.setResizable(false);
        window.add("Center", canvas);
        inputHandler = new GUIInputHandler(canvas, m);
        window.pack();
        Timer drawTime = new Timer(1, canvas);
        drawTime.start();
    }
    public void render(){
        canvas.repaint();
    }
    private class DrawingBoard extends JPanel implements ActionListener{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.fillRect(0,0,(int)Constants.WORLD_WIDTH,(int)Constants.WORLD_HEIGHT);
            for(GameObject o : model.getGameObjects()){
                o.render(g);
            }
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            repaint();
        }
        @Override
        public Dimension getPreferredSize(){
            return new Dimension((int)Constants.WORLD_WIDTH,(int)Constants.WORLD_HEIGHT);
        }
    }
}
