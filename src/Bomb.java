import bagel.Image;

public class Bomb extends Weapon {

    private final static int SHOOT_SPEED = 5;
    private final static int SHOOTING_RANGE = 50;

    public Bomb(int x, int y) {
        super(x, y, new Image("res/level-1/bomb.png"));
    }

    @Override
    public int shootingSpeed() {
        return SHOOT_SPEED;
    }

    @Override
    public int shootingRange() {
        return SHOOTING_RANGE;
    }
}