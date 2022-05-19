import java.awt.*;

public class Ray {

    public static final Color COLOR = Color.BLUE;

    private Point worldP1;
    private Point worldP2;
    private Point worldDisplayPoint;

    private Point screenP1;
    private Point screenP2;
    private Point screenDisplayPoint;

    public double dx;
    public double dy;

    private double angle;

    public Ray(Point worldP1, Point worldP2, int width, int height) {
        this.worldP1 = worldP1;
        this.worldP2 = worldP2;
        screenP1 = Main.worldToScreen(this.worldP1, width, height);
        screenP2 = Main.worldToScreen(this.worldP2, width, height);

        worldDisplayPoint = null;
        screenDisplayPoint = null;

        dx = worldP2.x - worldP1.x;
        dy = worldP2.y - worldP1.y;

        calculateAngle();
    }

    public void calculateAngle() {
        Point p2 = (worldDisplayPoint != null) ? worldDisplayPoint : worldP2;

        double dx = p2.x - worldP1.x;
        double dy = p2.y - worldP1.y;

        angle = Math.abs(Math.atan(dx/dy) * 180.0 / Math.PI);

        // straight up
        if (dx == 0 && dy < 0) {
            angle = 90;
        }
        // straight down
        else if (dx == 0 && dy > 0) {
            angle = 270;
        }
        // straight right
        else if (dy == 0 && dx > 0) {
            angle = 0;
        }
        // straight left
        else if (dy == 0 && dx < 0) {
            angle = 180;
        }
        // top right
        else if (dx > 0 && dy < 0) {
            angle = 90 - angle;
        }
        // top left
        else if (dx < 0 && dy < 0) {
            angle += 90;
        }
        // bottom left
        else if (dx < 0 && dy > 0) {
            angle = 90 - angle;
            angle += 180;
        }
        // bottom right
        else if (dx > 0 && dy > 0) {
            angle += 270;
        }

        angle = 360 - angle;
    }

    public void setEndPoint(Point p, int width, int height) {
        worldDisplayPoint = p;
        screenDisplayPoint = Main.worldToScreen(worldDisplayPoint, width, height);
    }

    public void restoreEndPoint() {
        worldP2.x = worldP1.x + dx;
        worldP2.y = worldP1.y + dy;

        worldDisplayPoint = null;
        screenDisplayPoint = null;
    }

    public void resize(int width, int height) {
        screenP1 = Main.worldToScreen(worldP1, width, height);
        screenP2 = Main.worldToScreen(worldP2, width, height);
        screenDisplayPoint = Main.worldToScreen(worldDisplayPoint, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ray))
            return false;

        Ray r = (Ray) o;
        return screenP1.equals(r.getScreenP1()) && screenP2.equals(r.getScreenP2());
    }

    public Point getWorldP1() {
        return worldP1;
    }

    public Point getWorldP2() {
        return worldP2;
    }

    public void setWorldP1(Point worldP1) {
        this.worldP1 = worldP1;
    }

    public Point getScreenP1() {
        return screenP1;
    }

    public void setScreenP1(Point screenP1) {
        this.screenP1 = screenP1;
    }

    public Point getScreenP2() {
        return screenP2;
    }

    public void setScreenP2(Point screenP2) {
        this.screenP2 = screenP2;
    }

    public Point getScreenDisplayPoint() {
        return screenDisplayPoint;
    }

    public double getAngle() {
        return angle;
    }

}
