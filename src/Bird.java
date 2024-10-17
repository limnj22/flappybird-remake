import bagel.*;
import bagel.util.*;

public class Bird {

    private final static int BIRD_X_POS = 200;
    private final static int BIRD_Y_POS = 350;
    private final static int FLYING_RATE = 6;
    private final static double MAX_FALLING_VELOCITY = 10;
    private final static double FALLING_ACCELERATION = 0.4;

    private boolean birdWingUp;
    private boolean birdFlying;
    private double birdY;
    private Image bird = new Image("res/birdWingDown.png");
    private Point location;
    private boolean carryingWeapon;

    public Bird() {
        location = new Point(BIRD_X_POS, BIRD_Y_POS);
    }

    public void render() {
        bird.draw(location.x, location.y);
    }

    /**
     * Update the bird's flapping animation.
     */
    public void updateAnimation(boolean lvlOne) {
        birdWingUp = !birdWingUp;
        if (lvlOne) {
            if (!birdWingUp) {
                bird = new Image("res/level-1/birdWingDown.png");
            } else {
                bird = new Image("res/level-1/birdWingUp.png");
            }
        } else {
            if (!birdWingUp) {
                bird = new Image("res/birdWingDown.png");
            } else {
                bird = new Image("res/birdWingUp.png");
            }
        }
    }

    /**
     * Apply gravity to the bird.
     */
    public void updateFalling(int frame) {
        birdY = location.y;
        if (FALLING_ACCELERATION * frame <= MAX_FALLING_VELOCITY) {
            birdY += FALLING_ACCELERATION * frame;
        } else {
            birdY += MAX_FALLING_VELOCITY;
        }
        location = new Point(location.x, birdY);
    }

    /**
     * Make the bird fly.
     */
    public void updateFlying() {
        birdY = location.y;
        birdY -= FLYING_RATE;
        location = new Point(location.x, birdY);
    }

    /**
     * Respawn the bird in its original position.
     */
    public void respawn() {
        birdY = BIRD_Y_POS;
        location = new Point(location.x, birdY);
    }

    public boolean getFlyingStatus() {
        return birdFlying;
    }

    public Point getLocation() {
        return location;
    }

    public void setFlyingStatus(boolean status) {
        this.birdFlying = status;
    }

    public boolean getCarryingWeapon() {
        return carryingWeapon;
    }

    public void setCarryingWeapon(boolean status) {
        carryingWeapon = status;
    }

}
