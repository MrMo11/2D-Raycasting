import java.awt.*;
import java.util.Collection;

public class Shape {

    //private Polygon poly;
    //private Polygon convertedPoly;

    private Point[] worldPoly;
    private Polygon screenPoly;

    public Shape(int width, int height, Point... pts) {
        //poly = new Polygon();
        //convertedPoly = new Polygon();

        worldPoly = new Point[pts.length];
        screenPoly = new Polygon();

        int i = 0;
        for (Point p : pts) {
            worldPoly[i] = new Point(p.x, p.y);

            Point screenPoint = Main.worldToScreen(p, width, height);
            screenPoly.addPoint((int) screenPoint.x, (int) screenPoint.y);
            i++;
        }
    }

    public Shape(Collection<Point> pts) {
        worldPoly = new Point[pts.size()];
        screenPoly = new Polygon();

        int i = 0;
        for (Point p : pts) {
            worldPoly[i] = new Point(p.x, p.y);
            screenPoly.addPoint((int)p.x, (int)p.y);
            i++;
        }
    }

    public void render(Graphics2D g, boolean fill) {
        if (fill) {
            g.setColor(Color.RED);
            g.fillPolygon(screenPoly);
        }
        else {
            g.setColor(Color.WHITE);
            g.drawPolygon(screenPoly);
        }
    }

    public void resize(int width, int height) {
        for (int i = 0; i < screenPoly.npoints; i++) {
            // convert point to screen coordinates
            Point screenPoint = Main.worldToScreen(worldPoly[i], width, height);
            screenPoly.xpoints[i] = (int) screenPoint.x;
            screenPoly.ypoints[i] = (int) screenPoint.y;
        }
    }

    public Point[] getPoints() {
        return worldPoly;
    }


}
