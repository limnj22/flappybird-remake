import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class SteelPipe extends Pipe {

    private final static int FLAME_RENDER_FRAMES = 20;

    private final Image flame = new Image("res/level-1/flame.png");
    private final DrawOptions imgMod = new DrawOptions();
    private int frame;
    private boolean flameOn;

    private final Rectangle flameRect;
    private Point flameLocation;
    private double flameX;
    private double flameY;

    public SteelPipe(int x, int y) {
        super(x, y, new Image("res/level-1/steelPipe.png"));
        flameLocation = new Point(flameX, flameY);
        flameRect = new Rectangle(flameLocation, flame.getWidth(), flame.getHeight());
    }

    @Override
    public void render(boolean topPipe) {
        if (getRender()) {
            if (topPipe) {
                getPipe().drawFromTopLeft(getX(), getY());
            } else {
                getPipe().drawFromTopLeft(getX(), getY(), imgMod.setRotation(-Math.PI));
            }
            renderFlames(topPipe);
        }
    }

    /**
     * Render the flames for 20 frames, every 20 frames.
     */
    public void renderFlames(boolean topPipe) {
        if (frame % FLAME_RENDER_FRAMES == 0) {
            flameOn = !flameOn;
        }
        if (flameOn) {
            if (topPipe) {
                flame.drawFromTopLeft(flameX = getX(), flameY = getPipeRect().bottom());
            } else {
                flame.drawFromTopLeft(flameX = getX(), flameY = getPipeRect().top()-flame.getHeight(), imgMod.setRotation(-Math.PI));
            }
            flameLocation = new Point(flameX, flameY);
            flameRect.moveTo(flameLocation);
        }
        frame++;
    }

    @Override
    public boolean isFlameOn() {
        return flameOn;
    }

    @Override
    public Rectangle getFlameRect() {
        return flameRect;
    }

    @Override
    public boolean isSteel() {
        return true;
    }
}
