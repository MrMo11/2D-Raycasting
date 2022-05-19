public class Point {

    public double x;
    public double y;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
