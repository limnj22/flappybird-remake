import bagel.*;
import bagel.util.*;
import java.lang.Math;

public abstract class Pipe {

    private final static double TIMESCALE_INCREASE_FACTOR = 1.5;
    private final static double TIMESCALE_DECREASE_FACTOR = 1/1.5;
    private final static int BASE_PIPE_SPEED = 3;
    private final static int BASE_TIMESCALE = 1;
    private final static int MAX_TIMESCALE = 5;

    private Image pipe;
    private final Rectangle pipeRect;
    private Point location;
    private double pipeX;
    private int timeScale = BASE_TIMESCALE;
    private static double movementSpeed = BASE_PIPE_SPEED;
    private boolean passed;
    private boolean render = true;

    public Pipe (int x, int y, Image img) {
        setImage(img);
        location = new Point(x, y);
        // each pipe needs a corresponding rectangle type for collision detection
        pipeRect = new Rectangle(location, pipe.getWidth(), pipe.getHeight());
    }

    /**
     * Render the pipe.
     */
    public void render(boolean topPipe) {
        DrawOptions imgMod = new DrawOptions();
        if (render) {
            if (topPipe) {
                pipe.drawFromTopLeft(location.x, location.y);
            } else {
                pipe.drawFromTopLeft(location.x, location.y, imgMod.setRotation(-Math.PI));
            }
        }
    }

    /**
     * Updates the movement of the pipe.
     */
    public void updateMovement() {
        pipeX = location.x;
        pipeX -= movementSpeed;
        location = new Point(pipeX, location.y);
        pipeRect.moveTo(location);
    }

    /**
     * Increases the timescale of the pipe.
     */
    public void increaseTimeScale() {
        if (timeScale < MAX_TIMESCALE) {
            movementSpeed *= TIMESCALE_INCREASE_FACTOR;
            timeScale++;
        }
    }

    /**
     * Decreases the timescale of the pipe.
     */
    public void decreaseTimeScale() {
        if (timeScale > BASE_TIMESCALE) {
            movementSpeed *= TIMESCALE_DECREASE_FACTOR;
            timeScale--;
        }
    }

    public void setImage(Image img) {
        pipe = img;
    }

    public Rectangle getPipeRect() {
        return pipeRect;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed() {
        passed = true;
    }

    public boolean isRenderable() {
        return render;
    }

    public void stopRendering() {
        render = false;
    }

    public double getX() {
        return location.x;
    }

    public double getY() {
        return location.y;
    }

    public Point getLocation() { return location; }

    public boolean getRender() { return render; }

    public Image getPipe() {
        return pipe;
    }

    public boolean isSteel() {
        return false;
    }

    public boolean isFlameOn() {
        return false;
    }

    public Rectangle getFlameRect() {
        return null;
    }

}
