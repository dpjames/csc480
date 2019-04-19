package Controller;

import Model.Model;

public class InputHandler {
    public static final double VELOCITY = 5;
    public Model model;
    public void movePlayer(double xa, double ya){
        model.movePlayer(xa,ya);
    }
}
