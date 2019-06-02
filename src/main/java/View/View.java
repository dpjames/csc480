package View;

import Controller.GUIInputHandler;
import Controller.InputHandler;
import Model.Model;
import Model.GameObject;
import Model.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ConcurrentModificationException;

public class View {
    private static final String TITLE = "A very COOL title";
    private JFrame window;
    private DrawingBoard canvas;
    private Model model;
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
        window.pack();
        Timer drawTime = new Timer(1, canvas);
        drawTime.start();
    }
    public void render(){
        canvas.repaint();
    }

    public JPanel getCanvas() {
        return canvas;
    }

    private class DrawingBoard extends JPanel implements ActionListener{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.fillRect(0,0,(int)Constants.WORLD_WIDTH,(int)Constants.WORLD_HEIGHT);
            try {
                for (GameObject o : model.getGameObjects()) {
                    o.render(g);
                }
            } catch (ConcurrentModificationException e){
                //yeah ignore this
            }
            g.setFont(new Font("Verdana", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString((int)(100 * model.getScore()) / 100.0 + "", 0 , 40);
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
