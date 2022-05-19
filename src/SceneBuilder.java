import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class SceneBuilder extends JPanel implements MouseListener, MouseMotionListener {

    private Scene scene;
    private BufferedImage img;
    private int width;
    private int height;

    private Color backgroundColor = Color.BLACK;

    private ArrayList<Point> screenPoints;
    private ArrayList<Point> worldPoints;
    private Point mousePoint;

    private Shape insideShape;

    public SceneBuilder(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        addMouseListener(this);
        addMouseMotionListener(this);

        scene = new Scene(width, height);
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        screenPoints = new ArrayList<>();
        worldPoints = new ArrayList<>();
        mousePoint = new Point();
    }

    public SceneBuilder(int width, int height, Scene scene) {
        this.width = width;
        this.height = height;
        this.scene = scene;

        setPreferredSize(new Dimension(width, height));
        addMouseListener(this);
        addMouseMotionListener(this);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        screenPoints = new ArrayList<>();
        worldPoints = new ArrayList<>();
        mousePoint = new Point();
    }

    public void update(double dt) {
        insideShape = getInsideShape();
    }

    public void render() {
        Graphics2D imgGraphics = (Graphics2D) img.getGraphics();

        // clear background
        imgGraphics.setColor(backgroundColor);
        imgGraphics.fillRect(0, 0, width, height);

        imgGraphics.setColor(Color.WHITE);
        if (screenPoints.size() > 0) {
            // render line from first point to mouse
            imgGraphics.drawLine((int) screenPoints.get(0).x, (int) screenPoints.get(0).y, (int) mousePoint.x, (int) mousePoint.y);
            // render line from previous point to mouse
            imgGraphics.drawLine((int) screenPoints.get(screenPoints.size() - 1).x, (int) screenPoints.get(screenPoints.size() - 1).y, (int) mousePoint.x, (int) mousePoint.y);
        }

        // render mouse point
        Main.renderPoint(imgGraphics, mousePoint, 10);

        // render scene and vertices
        renderVertices(imgGraphics);
        if (insideShape != null)
            insideShape.render(imgGraphics, true);

        scene.render(imgGraphics);

        //.drawLine((int)mousePoint.x, (int)mousePoint.y, (int)mousePoint.x, (int)(mousePoint.y - Main.HEIGHT));

        Graphics g = getGraphics();
        g.drawImage(img, 0, 0, null);
    }

    private void renderVertices(Graphics2D g) {
        try {
            for (int i = 0; i < screenPoints.size(); i++) {
                Main.renderPoint(g, screenPoints.get(i), 10);

                int x1 = (int) screenPoints.get(i).x;
                int y1 = (int) screenPoints.get(i).y;

                int x2 = x1;
                int y2 = y1;
                if (i + 1 < screenPoints.size()) {
                    x2 = (int) screenPoints.get((i + 1) % screenPoints.size()).x;
                    y2 = (int) screenPoints.get((i + 1) % screenPoints.size()).y;
                }

                g.drawLine(x1, y1, x2, y2);
            }
        } catch (ConcurrentModificationException e) {

        }
    }

    public Shape getInsideShape() {
        int i = 0;
        for (Shape s : scene.getShapes()) {
            if (i != 0 && isInside(Main.screenToWorld(mousePoint, width, height), s))
                return s;
            i++;
        }

        return null;
    }

    public static boolean isInside(Point p, Shape s) {
        return numRayIntersections(p, new Point(p.x, p.y - Main.HEIGHT), s) % 2 != 0;
    }

    public static int numRayIntersections(Point p1, Point p2, Shape s) {
        Point[] shapePoints = s.getPoints();
        int count = 0;

        for (int i = 0; i < shapePoints.length; i++) {
            Point intersectingPoint = Window.lineLineIntersection(p1, p2, shapePoints[i], shapePoints[(i + 1) % shapePoints.length]);
            if (intersectingPoint != null) {
                count++;
            }
        }

        return count;
    }


    public void changeSize(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scene.resize(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (Main.screenState == Main.Screen.MAIN) {
            Main.screenState = Main.Screen.SCENE_BUILDER;
            changeSize(Main.WIDTH, Main.HEIGHT);
        } else if (Main.screenState == Main.Screen.SCENE_BUILDER) {
            mousePoint.x = e.getX();
            mousePoint.y = e.getY();

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (insideShape == null) {
                    // add point
                    Point newPoint = new Point(e.getX(), e.getY());
                    screenPoints.add(newPoint);
                } else {
                    scene.getShapes().remove(insideShape);
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                // convert points to world coordinates
                if (!screenPoints.isEmpty() && screenPoints.size() > 2) {
                    createIntersectionPoints();

                    for (Point screenPoint : screenPoints)
                        worldPoints.add(Main.screenToWorld(screenPoint, width, height));

                    scene.add(new Shape(worldPoints));
                    scene.resize(width, height);
                    screenPoints.clear();
                    worldPoints.clear();
                }
            } else if (SwingUtilities.isMiddleMouseButton(e)) {
                changeSize(Main.WIDTH, Main.HEIGHT / 3);
                Main.scene = scene;
                Main.changeScene = true;
                Main.screenState = Main.Screen.MAIN;
            }
        }

    }

    private void createIntersectionPoints() {
        //ArrayList<Point> temp = new ArrayList<>();
        int length = screenPoints.size();
        for (int i = 0; i < length - 1; i++) {
            Point start = screenPoints.get(i);
            Point end = screenPoints.get(i + 1);

            System.out.println("start: " + start);
            System.out.println("end: " + end);
            for (int j = 0; j != i && j != (i + 1) && j < length; j++) {
                Point end2 = screenPoints.get(j);
                System.out.println("end2: " + end2);

                Point intersectionPoint = Window.lineLineIntersection(start, end, start, end2);
                if (intersectionPoint != null && (!intersectionPoint.equals(start) && !intersectionPoint.equals(end) && !intersectionPoint.equals(end2))) {
                    System.out.println("adding point");
                    System.out.println("intersectionPoint: " + intersectionPoint);
                    screenPoints.add(intersectionPoint);
                }

            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePoint.x = e.getX();
        mousePoint.y = e.getY();
    }
}
