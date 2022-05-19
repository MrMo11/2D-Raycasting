import java.awt.*;
import java.util.LinkedList;

public abstract class RayPoint {

    public static final int POINT_RADIUS = 10;

    protected LinkedList<Ray> rays;
    protected Point p;

    protected int width;
    protected int height;
    protected boolean showRayLines = false;

    public RayPoint(Point p, int width, int height) {
        this.p = p;
        this.width = width;
        this.height = height;

        rays = new LinkedList<>();
    }

    public abstract void setOrigin(Scene scene, Point p);

    public void render(Graphics2D g) {
        // create filled rays
        int[] xpoints = new int[rays.size()];
        int[] ypoints = new int[rays.size()];
        int i = 0;
        for (Ray r : rays) {
            if (r.getScreenDisplayPoint() != null) {
                xpoints[i] = (int) r.getScreenDisplayPoint().x;
                ypoints[i] = (int) r.getScreenDisplayPoint().y;
            } else {
                xpoints[i] = (int) r.getScreenP2().x;
                ypoints[i] = (int) r.getScreenP2().y;
            }
            i++;
        }

        // draw filled rays
        g.setColor(Color.WHITE);
        g.fillPolygon(xpoints, ypoints, rays.size());

        // draw rays
        if (showRayLines) {
            System.out.println(":jdskfljs");
            i = 0;
            for (Ray r : rays) {
                g.setColor(Ray.COLOR);
                if (r.getScreenDisplayPoint() != null) {
                    g.drawLine((int) r.getScreenP1().x, (int) r.getScreenP1().y, (int) r.getScreenDisplayPoint().x, (int) r.getScreenDisplayPoint().y);
                    Main.renderPoint(g, r.getScreenDisplayPoint(), RayPoint.POINT_RADIUS);

                    //g.setColor(Color.RED);
                    //g.drawString("" + r.getAngle(), (float) r.getScreenDisplayPoint().x, (float) r.getScreenDisplayPoint().y);
                } else {
                    g.drawLine((int) r.getScreenP1().x, (int) r.getScreenP1().y, (int) r.getScreenP2().x, (int) r.getScreenP2().y);
                    Main.renderPoint(g, r.getScreenP2(), RayPoint.POINT_RADIUS);

                    //g.setColor(Color.RED);
                    //g.drawString("" + r.getAngle(), (float) r.getScreenP2().x, (float) r.getScreenP2().y);
                }

                i++;
            }
        }
    }

    private double getCenterX(int x, int radius) {
        return x - radius;
    }

    private double getCenterY(int y, int radius) {
        return y - radius;
    }

    public void addRay(Ray r) {
        rays.add(r);
    }

    public LinkedList<Ray> getRays() {
        return rays;
    }

    public void resize(int width, int height) {
        for (Ray r : rays)
            r.resize(width, height);

        this.width = width;
        this.height = height;
    }

    public void toggleRayLines() {
        showRayLines = !showRayLines;
    }

}
