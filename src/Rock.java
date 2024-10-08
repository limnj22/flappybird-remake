import bagel.Image;

public class Rock extends Weapon {

    private final static int SHOOT_SPEED = 5;
    private final static int SHOOTING_RANGE = 25;

    public Rock(int x, int y) {
        super(x, y, new Image("res/level-1/rock.png"));
    }

    @Override
    public int shootingSpeed() {
        return SHOOT_SPEED;
    }

    @Override
    public int shootingRange() {
        return SHOOTING_RANGE;
    }

    @Override
    public boolean isRock() {
        return true;
    }
}
