import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

public class Window extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    private RayPoint rp;
    private BufferedImage img;

    private int mouseX = 0;
    private int mouseY = 0;
    boolean mouseMoved = false;

    private Color backgroundColor = Color.BLACK;

    private Scene scene;
    private int width;
    private int height;


    public Window(RayPoint rp, int width, int height) {
        setPreferredSize(new Dimension(width, height));

        this.width = width;
        this.height = height;
        this.rp = rp;

        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);


        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scene = new Scene(width, height);
    }

    public void update(double dt) {
        if (mouseMoved)
            rp.setOrigin(scene, Main.screenToWorld(new Point(mouseX, mouseY), width, height));
        rayIntersection(rp, scene, width, height);
    }

    public void render() {
        Graphics2D imgGraphics = (Graphics2D) img.getGraphics();
        // clear background
        imgGraphics.setColor(backgroundColor);
        imgGraphics.fillRect(0, 0, width, height);

        if (scene != null)
            scene.render(imgGraphics);

        rp.render(imgGraphics);

        Graphics g = getGraphics();
        g.drawImage(img, 0, 0, null);
    }

    public static Circle createCircle(double x, double y, int radius) {
        return new Circle(x - radius / 2.0, y - radius / 2.0, radius);
    }

    public static LinkedList<Point> rayIntersection(RayPoint ray, Scene scene, int width, int height) {
        LinkedList<Point> intersectingPoints = new LinkedList<>();

        for (Ray r : ray.getRays()) {
            Point intersectingPoint = Window.getIntersectingPoint(r, scene);
            if (intersectingPoint != null) {
                intersectingPoints.add(intersectingPoint);
                r.setEndPoint(intersectingPoint, width, height);
            } else {
                r.restoreEndPoint();
            }
        }

        return intersectingPoints;
    }

    private static Point getIntersectingPoint(Ray r, Scene scene) {
        Point p = null;
        if (scene != null) {
            for (Shape s : scene.getShapes()) {
                Point intersectingPoint = Window.lineShapeIntersection(r.getWorldP1(), r.getWorldP2(), s);

                if (intersectingPoint != null) {
                    if (p == null) {
                        p = intersectingPoint;
                    } else {
                        double dx1 = p.x - r.getWorldP1().x;
                        double dy1 = p.y - r.getWorldP1().y;
                        double dist1 = dx1 * dx1 + dy1 * dy1;

                        double dx2 = intersectingPoint.x - r.getWorldP1().x;
                        double dy2 = intersectingPoint.y - r.getWorldP1().y;
                        double dist2 = dx2 * dx2 + dy2 * dy2;

                        if (dist2 < dist1)
                            p = intersectingPoint;
                    }
                }
            }
        }


        return p;
    }

    public Point lineRectIntersection(Point p1, Point p2, Rectangle r) {
        // points of rectangle r
        Point topLeft = new Point(r.x, r.y);
        Point topRight = new Point(r.x + r.width, r.y);
        Point botLeft = new Point(r.x, r.y + r.height);
        Point botRight = new Point(r.x + r.width, r.y + r.height);

        Point left = lineLineIntersection(p1, p2, topLeft, botLeft);
        Point right = lineLineIntersection(p1, p2, topRight, botRight);
        Point top = lineLineIntersection(p1, p2, topLeft, topRight);
        Point bot = lineLineIntersection(p1, p2, botLeft, botRight);

        Point[] points = {left, right, top, bot};
       /* int minIndex = 0;
        double min = 0;
        // whether or not the first valid value was set to min
        boolean set = false;

        for (int i = 0; i < points.length; i++) {
            if (points[i] != null) {
                double dx = p1.x - points[i].x;
                double dy = p1.y - points[i].y;
                double dist = dx * dx + dy * dy;
                if (!set) {
                    min = dist;
                    minIndex = i;
                    set = true;
                } else if (min > dist) {
                    min = dist;
                    minIndex = i;
                }
            }
        }

        return points[minIndex];*/
        return getMinPoint(p1, points);
    }

    public static Point getMinPoint(Point origin, Point[] points) {
        Point minPoint = null;
        double minDist = 0;

        // whether or not the first valid value was set to min
        boolean set = false;

        for (Point point : points) {
            if (point != null) {
                double dx = origin.x - point.x;
                double dy = origin.y - point.y;
                double dist = dx * dx + dy * dy;
                if (!set) {
                    minDist = dist;
                    minPoint = point;
                    set = true;
                } else if (minDist > dist) {
                    minDist = dist;
                    minPoint = point;
                }
            }
        }

        return minPoint;
    }

    public static Point getMinPoint(Point origin, Collection<Point> points) {
        Point minPoint = null;
        double minDist = 0;

        // whether or not the first valid value was set to min
        boolean set = false;

        for (Point point : points) {
            if (point != null) {
                double dx = origin.x - point.x;
                double dy = origin.y - point.y;
                double dist = dx * dx + dy * dy;
                if (!set) {
                    minDist = dist;
                    minPoint = point;
                    set = true;
                } else if (minDist > dist) {
                    minDist = dist;
                    minPoint = point;
                }
            }
        }

        return minPoint;
    }

    public static Point lineShapeIntersection(Point p1, Point p2, Shape s) {
        LinkedList<Point> intersectionPoints = new LinkedList<>();
        Point[] shapePoints = s.getPoints();

        for (int i = 0; i < shapePoints.length; i++) {
            Point intersectingPoint = Window.lineLineIntersection(p1, p2, shapePoints[i], shapePoints[(i + 1) % shapePoints.length]);
            if (intersectingPoint != null)
                intersectionPoints.add(intersectingPoint);
        }

        return Window.getMinPoint(p1, intersectionPoints);
    }

    public static Point lineLineIntersection(Point p1, Point p2, Point p3, Point p4) {
        double denom = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x);

        if (denom == 0)
            return null;

        double t = ((p1.x - p3.x) * (p3.y - p4.y) - (p1.y - p3.y) * (p3.x - p4.x)) / denom;
        double u = -((p1.x - p2.x) * (p1.y - p3.y) - (p1.y - p2.y) * (p1.x - p3.x)) / denom;

        if ((t >= 0 && t <= 1) && (u >= 0 && u <= 1)) {
            double x = p1.x + (t * (p2.x - p1.x));
            double y = p1.y + (t * (p2.y - p1.y));

            return new Point(x, y);
        }

        return null;
    }

    public void changeSize(int width, int height) {
        scene.resize(width, height);
        rp.resize(width, height);
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);


        this.width = width;
        this.height = height;
    }

    public RayPoint getRayPoint() {
        return rp;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }


    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseMoved = true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (Main.screenState == Main.Screen.MAIN) {
            if (rp instanceof RayAll)
                Main.screenState = Main.Screen.RAY_ALL;
            else
                Main.screenState = Main.Screen.RAY_END;

            changeSize(Main.WIDTH, Main.HEIGHT);
            //requestFocus();
        } else if (Main.screenState == Main.Screen.RAY_ALL) {
            if (SwingUtilities.isMiddleMouseButton(e)) {
                changeSize(Main.WIDTH, Main.HEIGHT / 3);
                Main.screenState = Main.Screen.MAIN;
                //transferFocus();
            }
        } else if (Main.screenState == Main.Screen.RAY_END) {
            if (SwingUtilities.isMiddleMouseButton(e)) {
                changeSize(Main.WIDTH, Main.HEIGHT / 3);
                Main.screenState = Main.Screen.MAIN;
                //transferFocus();
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        requestFocus();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        transferFocus();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyChar() + " pressed");
        if (KeyEvent.VK_D == Character.toUpperCase(e.getKeyChar())) {
            System.out.println("D PRESSED");
            rp.toggleRayLines();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
