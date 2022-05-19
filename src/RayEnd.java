import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;

public class RayEnd extends RayPoint {

    public RayEnd(Point p, int width, int height, Scene scene) {
        super(p, width, height);
        initRays(scene);
    }

    public void initRays(Scene scene) {
        rays.clear();
        if (scene != null) {
            for (Shape s : scene.getShapes()) {
                for (Point sp : s.getPoints()) {
                    // original ray
                    Ray r = new Ray(p, sp, width, height);

                    double offset = 0.0001;
                    double angle = r.getAngle() * Math.PI / 180.0;
                    double nAngle = angle - offset;
                    double pAngle = angle + offset;
                    double radius = 50;

                    Ray r2 = new Ray(p, new Point(p.x + (radius * Math.cos(nAngle)), p.y + (radius * Math.sin(nAngle))), width, height);
                    // ray offset by negative angle
                    rays.add(r2);
                    //original ray
                    rays.add(r);
                    // ray offset by positive angle
                    Ray r3 = new Ray(p, new Point(p.x + (radius * Math.cos(pAngle)), p.y + (radius * Math.sin(pAngle))), width, height);
                    rays.add(r3);
                }
            }
        }

        rays.sort((r1, r2) -> {
            double diff = r1.getAngle() - r2.getAngle();

            if (diff < 0)
                return -1;

            if (diff > 0)
                return 1;

            return 0;
        });
    }

    @Override
    public void setOrigin(Scene scene, Point p) {
        this.p = p;
        initRays(scene);
    }

}
