import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;
import bagel.util.Rectangle;

public abstract class Weapon {

    private final static int BASE_MOVEMENT_SPEED = 3;

    private Image weapon;
    private Point location;
    private double weaponX;
    private double weaponY;
    private int distanceTravelled;
    private boolean pickedUp;
    private boolean fired;
    private boolean render = true;

    public Weapon(int x, int y, Image img) {
        setImage(img);
        location = new Point(x, y);
    }

    /**
     * Render the weapon.
     */
    public void render() {
        if (render) {
            weapon.draw(location.x, location.y);
        }
    }

    /**
     * Update the movement of the weapon depending on if it has been picked up or fired.
     */
    public void updateMovement(Bird bird, Input input) {
        if (!bird.getCarryingWeapon() && getRect().intersects(bird.getLocation())) {
            pickedUp = true;
        }
        if (!pickedUp) {
            weaponX = location.x;
            weaponX -= BASE_MOVEMENT_SPEED;
            location = new Point(weaponX, location.y);
        } else if (!fired) {
            weaponX = bird.getLocation().x;
            weaponY = bird.getLocation().y;
            location = new Point(weaponX, weaponY);
            bird.setCarryingWeapon(true);
            detectFiring(input);
        }
        if (fired) {
            shoot();
            bird.setCarryingWeapon(false);
        }
    }

    /**
     * Detect input to fire weapon.
     */
    public void detectFiring(Input input) {
        if (input.wasPressed(Keys.S)) {
            fired = true;
        }
    }

    /**
     * Make the weapon follow its trajectory once fired.
     */
    public void shoot() {
        if (distanceTravelled <= shootingRange()) {
            weaponX = getWeaponLocation().x;
            weaponX += shootingSpeed();
            distanceTravelled += shootingSpeed();
            setWeaponLocation(weaponX, getWeaponLocation().y);
        } else {
            render = false;
        }
    }

    public void setImage(Image img) {
        weapon = img;
    }

    public Rectangle getRect() {
        return weapon.getBoundingBoxAt(location);
    }

    public Point getWeaponLocation() {
        return location;
    }

    public void setWeaponLocation(double x, double y) {
        location = new Point(x, y);
    }

    public boolean isFired() {
        return fired;
    }

    public boolean getRender() {
        return render;
    }

    public boolean isRock() {
        return false;
    }

    public abstract int shootingRange();

    public abstract int shootingSpeed();

}
