package Model;


public class Enemy extends GameObject {
    public boolean inbounds;
    static int globWidth = 10;
    static int globHeight = 10;
    static Player player;
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
        speedmod = 2;
        //globHeight = 10;
        //globWidth = 10;
    }

    void regen(){
        width = (int) (Math.random() * 100 + 10);
        height = width;
        do {
            this.px = Math.random() * 3 * Constants.WORLD_WIDTH - Constants.WORLD_WIDTH;
            this.py = Math.random() * 3 * Constants.WORLD_HEIGHT - Constants.WORLD_HEIGHT;
        } while(checkInBounds());
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
        //this.vx = player.getPosition()[0] - this.px;
        //this.vy = player.getPosition()[1] - this.py;
        //double normScale = Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2));
        //this.vx /= normScale / speedmod;
        //this.vy /= normScale / speedmod;
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
        return new double[]{px, py};
    }
}
