package Model;

import java.awt.*;

public interface GameObject {
    void update(double deltaT);
    void render(Graphics g);
}
