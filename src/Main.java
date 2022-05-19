import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Main extends Canvas implements Runnable {

    public static boolean changeScene = false;
    public static Scene scene;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private boolean running = false;

    private BufferedImage img;
    private Color backgroundColor = Color.BLACK;

    private SceneBuilder sceneBuilder;
    private Window rayAllWindow;
    private Window rayEndWindow;

    public enum Screen {
        MAIN,
        SCENE_BUILDER,
        RAY_ALL,
        RAY_END
    }

    public static Screen screenState = Screen.MAIN;
    JFrame frame = new JFrame("2D Raycast Visualizer");

    public Main() {
        // setPreferredSize(new Dimension(0, 0));
        int w = WIDTH;
        int h = HEIGHT / 3;

        scene = Scene.getDefaultScene(w, h);//new Scene(w, h);
        sceneBuilder = new SceneBuilder(w, h, scene);

        Point rayOrigin = new Point(0, 0);
        rayAllWindow = new Window(new RayAll(rayOrigin, w, h, RayAll.DEFAULT_THETA, RayAll.DEFAULT_OFFSET), w, h);
        rayEndWindow = new Window(new RayEnd(rayOrigin, w, h, scene), w, h);
        rayAllWindow.setScene(scene);
        rayEndWindow.setScene(scene);

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.setLocationByPlatform(true);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        p.add(sceneBuilder);
        p.add(rayAllWindow);
        p.add(rayEndWindow);
        p.add(this);

        setVisible(false);

        frame.add(p);
        frame.pack();
        frame.setVisible(true);

        img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        start();
    }


    public synchronized void start() {
        Thread thread = new Thread(this);
        thread.start();
        running = true;
    }

    /**
     * Loop
     * Taken from http://www.java-gaming.org/index.php?topic=24220.0
     */
    public void run() {
        //This value would probably be stored elsewhere.
        final double GAME_HERTZ = 144;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        double lastRenderTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;

            //if (true) {
            //Do as many game updates as we need to, potentially playing catchup.
            while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                update((now - lastUpdateTime) / 1000000000.0);
                lastUpdateTime += TIME_BETWEEN_UPDATES;
                updateCount++;
            }

            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                lastUpdateTime = now - TIME_BETWEEN_UPDATES;
            }

            //Render. To do so, we need to calculate interpolation for a smooth render.
            //float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
            render();
            lastRenderTime = now;

            //Update the frames we got.
            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                //System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                //int fps = frameCount;
                //frameCount = 0;
                lastSecondTime = thisSecond;
            }

            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                Thread.yield();

                //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }

                now = System.nanoTime();
            }
            //}
        }
    }

    private void update(double dt) {
        if (changeScene) {
            rayAllWindow.setScene(scene);
            rayEndWindow.setScene(scene);

            RayEnd rp = ((RayEnd) rayEndWindow.getRayPoint());
            rp.initRays(scene);

            changeScene = false;
        }

        if (screenState == Screen.MAIN) {
            sceneBuilder.update(dt);
            rayAllWindow.update(dt);
            rayEndWindow.update(dt);
        } else if (screenState == Screen.SCENE_BUILDER) {
            sceneBuilder.update(dt);
        } else if (screenState == Screen.RAY_ALL) {
            rayAllWindow.update(dt);
        } else if (screenState == Screen.RAY_END) {
            rayEndWindow.update(dt);
        }


    }

    /**
     * Render
     * Idea taken from https://www.youtube.com/watch?v=Zh7YiiEuJFw
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D imgGraphics = (Graphics2D) img.getGraphics();

        // clear background
        imgGraphics.setColor(backgroundColor);
        imgGraphics.fillRect(0, 0, WIDTH, HEIGHT);

        // render scene
        if (screenState == Screen.MAIN) {
            sceneBuilder.setVisible(true);
            rayAllWindow.setVisible(true);
            rayEndWindow.setVisible(true);
            sceneBuilder.render();
            rayAllWindow.render();
            rayEndWindow.render();
        } else if (screenState == Screen.SCENE_BUILDER) {
            rayAllWindow.setVisible(false);
            rayEndWindow.setVisible(false);
            sceneBuilder.render();
        } else if (screenState == Screen.RAY_ALL) {
            sceneBuilder.setVisible(false);
            rayEndWindow.setVisible(false);
            rayAllWindow.render();
        } else if (screenState == Screen.RAY_END) {
            sceneBuilder.setVisible(false);
            rayAllWindow.setVisible(false);
            rayEndWindow.render();
        }

        // render image
        Graphics g = bs.getDrawGraphics();
        g.drawImage(img, 0, 0, null);

        g.dispose();
        bs.show();
    }

    public static Point worldToScreen(Point worldPoint, int width, int height) {
        if (worldPoint == null)
            return null;

        double screenX = (worldPoint.x + 1) * (0.5 * width);
        double screenY = (1 - worldPoint.y) * (0.5 * height);

        return new Point(screenX, screenY);
    }

    public static Point screenToWorld(Point screenPoint, int width, int height) {
        if (screenPoint == null)
            return null;

        double worldX = ((screenPoint.x + screenPoint.x) / (double) width) - 1;
        double worldY = 1 - ((screenPoint.y + screenPoint.y) / (double) height);

        return new Point(worldX, worldY);
    }

    public static void renderPoint(Graphics2D g, Point p, int radius) {
        Circle mouse = Window.createCircle(p.x, p.y, radius);
        g.fillOval((int) mouse.getCenterX(), (int) mouse.getCenterY(), (int) mouse.getRadius(), (int) mouse.getRadius());
    }

    public static void main(String[] args) {
        new Main();
    }
}

