import sun.awt.image.ImageWatched;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;

public class Scene {

    //private Shape boundary;
    private LinkedList<Shape> shapes;

    public Scene(int width, int height) {
        Shape boundary = new Shape(width, height, new Point(-1, 1),
                new Point(1, 1),
                new Point(1, -1),
                new Point(-1, -1));

        shapes = new LinkedList<>();
        shapes.add(boundary);
    }

    public Scene(LinkedList<Shape> shapes) {
        this.shapes = shapes;
    }

    public void render(Graphics2D g) {
        for (Shape s : shapes)
            s.render(g, false);
    }

    public void add(Shape s) {
        shapes.add(s);
    }

    public void resize(int width, int height) {
        for (Shape s : shapes)
            s.resize(width, height);
    }

    public LinkedList<Shape> getShapes() {
        return shapes;
    }

    public String toString() {
        return shapes.toString();
    }

    public static Scene getDefaultScene(int width, int height) {
        Shape boundary = new Shape(width, height, new Point(-1, 1),
                new Point(1, 1),
                new Point(1, -1),
                new Point(-1, -1));


        Shape triangle = new Shape(width, height, new Point(-0.5, 0.8), new Point(0.2,0.5), new Point(-0.5, 0.5));
        Shape square = new Shape(width, height, new Point(0.2, -0.1), new Point(0.5, -0.1), new Point(0.5, -0.4), new Point(0.2, -0.4));

        LinkedList<Shape> shapes = new LinkedList<>();
        shapes.add(boundary);
        shapes.add(triangle);
        shapes.add(square);

        return new Scene(shapes);
    }

}
