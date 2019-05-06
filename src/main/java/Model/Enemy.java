package Model;


public class Enemy extends GameObject {
    public boolean inbounds;
    public Enemy(){
        super();
        this.regen();
    }
    void regen(){
            this.px = Math.random() * 2 * Constants.WORLD_WIDTH - Constants.WORLD_WIDTH;
            this.px = Math.random() * 2 * Constants.WORLD_HEIGHT - Constants.WORLD_HEIGHT;
            double origin[] = {Constants.WORLD_WIDTH / 2.0, Constants.WORLD_HEIGHT / 2.0};
            this.vx = origin[0] - this.px;
            this.vy = origin[1] - this.py;
            //normalize speed;
            double normScale = Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2));
            this.vx /= normScale / 3;
            this.vy /= normScale / 3;
        this.inbounds = false;
    }
    @Override
    public void update(double deltaT) {
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

}
