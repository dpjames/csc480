package Model;


public class Enemy extends GameObject {
    public boolean inbounds;
    static int globWidth = 10;
    static int globHeight = 10;
    private static double speedmod = 3;
    public Enemy(){
        super();
        this.regen();
    }

    public static void hit() {
        //globWidth+=10;
        //globHeight+=10;
        speedmod*=1.15;
    }

    public static void reset() {
        speedmod = 3;
        //globHeight = 10;
        //globWidth = 10;
    }

    void regen(){
        width = (int) (Math.random() * 100 + 10);
        height = width;
        this.px = Math.random() * 2 * Constants.WORLD_WIDTH - Constants.WORLD_WIDTH;
        this.px = Math.random() * 2 * Constants.WORLD_HEIGHT - Constants.WORLD_HEIGHT;
        this.vx = (Math.random() * Constants.WORLD_WIDTH)  - px;
        this.vy = (Math.random() * Constants.WORLD_HEIGHT) - py;
        //normalize speed;
        double normScale = Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2));
        this.vx /= normScale / speedmod;
        this.vy /= normScale / speedmod;
        this.inbounds = false;
    }
    @Override
    public void update(double deltaT) {
        //this.width = globWidth;
        //this.height = globHeight;
        super.update(deltaT);
        if(this.inbounds){
            if(!checkInBounds()){
                this.inbounds = false;
                this.regen();
            }
        } else {
            this.inbounds = checkInBounds();
        }
    }

    public double[] getPosition() {
        return new double[]{py, px};
    }
}
