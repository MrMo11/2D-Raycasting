import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class RayAll extends RayPoint {

    public static double DEFAULT_THETA = 2;
    public static double DEFAULT_OFFSET = 10;

    public RayAll(Point p, int width, int height, double theta, double offset) {
        super(p, width, height);

        for (double angle = 0; angle < 360; angle += theta) {
            int radius = 10;
            double rOffsetX = (radius + offset) * Math.cos(angle * Math.PI / 180.0);
            double rOffsetY = (radius + offset) * Math.sin(angle * Math.PI / 180.0);
            double x = p.x + rOffsetX;
            double y = p.y + rOffsetY;
            Point endPoint = new Point(x, y);

            rays.add(new Ray(p, endPoint, width, height));
        }
    }

    @Override
    public void setOrigin(Scene scene, Point p) {
        double dx = p.x - this.p.x;
        double dy = p.y - this.p.y;

        for (Ray r : rays) {
            // new origin
            r.setWorldP1(p);

            // offset rays' endpoints
            r.getWorldP2().x += dx;
            r.getWorldP2().y += dy;

            r.setScreenP1(Main.worldToScreen(r.getWorldP1(), width, height));
            r.setScreenP2(Main.worldToScreen(r.getWorldP2(), width, height));
        }

        this.p = p;
    }

}
