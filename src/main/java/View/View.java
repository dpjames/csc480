package View;

import Controller.GUIInputHandler;
import Model.Model;
import Model.GameObject;
import Model.Constants;
import org.apache.commons.lang3.ObjectUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JFrame;


public class View {
    private static final String TITLE = "SPACE_ACE";
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

    public JPanel getCanvas() {
        return canvas;
    }

    public BufferedImage img;

    private class DrawingBoard extends JPanel implements ActionListener{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            if (img == null) {
                try {
                    //System.out.println(System.getProperty("user.dir"));
                    //System.out.println(System.getProperty("user.dir") + "/src/main/java/View/game.jpg");
                    img = ImageIO.read(new File(System.getProperty("user.dir") + "/src/main/java/View/game.jpg"));

                } catch (IOException e) {

                    e.printStackTrace();
                    img = null;
                }
            }
            g.drawImage(img, 0, 0, this);

            for(GameObject o : model.getGameObjects())
            {

                o.render(g);
            }

            g.setFont(new Font("Trebuchet MS Italic", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString((int)(100 * model.getScore()) / 100.0 + "", 10 , 40);
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
