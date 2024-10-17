import bagel.*;
import java.util.ArrayList;

public class LifeBar {

    private final static int FIRST_HEART_X_POS = 100;
    private final static int FIRST_HEART_Y_POS = 15;
    private final static int HEART_X_OFFSET = 50;

    private final Image heart = new Image("res/level/fullLife.png");
    private final ArrayList<Image> lifeBar = new ArrayList<>();
    private int currHeart;
    private boolean replenished;

    public LifeBar() {
        for (int i=0; i<3; i++) {
            lifeBar.add(heart);
        }
        currHeart = lifeBar.size();
    }

    /**
     * Render the life bar.
     */
    public void render() {
        for (int i=0; i< lifeBar.size(); i++) {
            lifeBar.get(i).drawFromTopLeft(FIRST_HEART_X_POS + i*HEART_X_OFFSET, FIRST_HEART_Y_POS);
        }
    }

    /**
     * Replenish lost lives and add three new lives for level one.
     */
    public void replenishLives() {
        // we only wish to replenish the life bar once
        if (!replenished) {
            for (int i=0; i<lifeBar.size(); i++) {
                lifeBar.remove(i);
                lifeBar.add(i, new Image("res/level/fullLife.png"));
            }
            // add another three lives to the life bar
            for (int i=0; i<3; i++) {
                lifeBar.add(heart);
            }
            currHeart = lifeBar.size();
            replenished = true;
        }
    }

    /**
     * Deduct a life.
     */
    public void loseLife() {
        if (currHeart > 0) {
            lifeBar.remove(currHeart-1);
            lifeBar.add(currHeart-1, new Image("res/level/noLife.png"));
            currHeart--;
        }
    }

    /**
     * Return the current life.
     */
    public int getCurrHeart() {
        return currHeart;
    }
}
