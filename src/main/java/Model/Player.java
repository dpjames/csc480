package Model;

public class Player implements GameObject{
    private double vx, vy, ax, ay, px, py; //velocity, acceleration, position;

    public Player(){
        this.ax = 1;
        this.ay = -1;
    }

    @Override
    public void update(double deltaT) {
        px+=.5 * ax * deltaT * deltaT + vx * deltaT;
        py+=.5 * ay * deltaT * deltaT + vy * deltaT;
    }

    @Override
    public void render() {

    }
}
