import Controller.Controller;
import Model.Model;
import View.View;
import Controller.AIInputHandler;
import org.tensorflow.*;

import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        /********************************************
         *        begin tensor flow test            *
         ********************************************/
        try (Graph g = new Graph()) {
            final String value = "Hello from " + TensorFlow.version();

            // Construct the computation graph with a single operation, a constant
            // named "MyConst" with a value "value".
            try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
                // The Java API doesn't yet include convenience functions for adding operations.
                g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
            }

            // Execute the "MyConst" operation in a Session.
            try (Session s = new Session(g);
                 // Generally, there may be multiple output tensors,
                 // all of them must be closed to prevent resource leaks.
                 Tensor output = (Tensor) s.runner().fetch("MyConst").run().get(0)) {
                System.out.println(new String(output.bytesValue(), "UTF-8"));
            }
        }
        /********************************************
         *          end tensor flow test            *
         ********************************************/

        Model m = new Model();
        //View v = new View(m);
        Controller c = new Controller(m, null, new AIInputHandler(m));
        c.gameLoop();

    }

}
