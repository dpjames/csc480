package View;

import Model.Model;
import Model.GameObject;
public class View {
    public void render(Model model) {
        //need to generate graphics and such here.
        //chances are this will be replaced with a "draw" or "update" function when this class is extended.
        for(GameObject o : model.getGameObjects()){
            o.render();
        }
    }
}
