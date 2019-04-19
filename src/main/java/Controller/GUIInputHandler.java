package Controller;
import Model.Model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GUIInputHandler extends InputHandler {

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

    public GUIInputHandler(JPanel canvas, Model m) {
        System.out.println("init action maps");
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0), LEFT);
        canvas.getActionMap().put(LEFT,left);


        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0), RIGHT);
        canvas.getActionMap().put(RIGHT,right);


        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W,0), UP);
        canvas.getActionMap().put(UP,up);

        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0), DOWN);
        canvas.getActionMap().put(DOWN,down);
        this.model = m;
    }

}
