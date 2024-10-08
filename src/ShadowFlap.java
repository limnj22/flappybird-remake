import bagel.*;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.Random;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2021
 *
 * Please filling your name below
 * @author: Nicholas Lim
 */
public class ShadowFlap extends AbstractGame {

    // GAME MESSAGES
    private final static String INSTRUCTION_MSG = "PRESS SPACE TO START";
    private final static String LVL_UP_MSG = "LEVEL-UP!";
    private final static String SHOOT_INSTRUCTION = "PRESS 'S' TO SHOOT";
    private final static String GAME_OVER_MSG = "GAME OVER!";
    private final static String CONGRATS_MSG = "CONGRATULATIONS!";
    private final static String SCORE_MSG = "SCORE: ";
    private final static String FINAL_SCORE_MSG = "FINAL SCORE: ";

    // PROGRAM CONSTANTS
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static int FONT_SIZE = 48;

    // GAME MESSAGE STATS
    private final static int SCORE_MSG_OFFSET = 75;
    private final static int SHOOT_MSG_OFFSET = 68;
    private final static int SHOOT_MSG_DISPLAY_TIME = 60;
    private final static int SCORE_COUNTER_X_POS = 100;
    private final static int SCORE_COUNTER_Y_POS = 100;

    // BIRD CONSTANTS
    private final static int FLAP_ANIM_FRAMES = 10;
    private final static int FLY_FRAMES = 10;

    // PIPE CONSTANTS
    private final static int PIPE_X_POS = 1024;
    private final static int PIPE_Y_POS = (768/2) + (168/2);
    private final static int PIPE_SPAWN_RATE = 100;
    private final static int HIGH_GAP_ADJUST = 200;
    private final static int HIGH_MID_GAP_ADJUST = 100;
    private final static int MID_GAP_ADJUST = 0;
    private final static int LOW_MID_GAP_ADJUST = -100;
    private final static int LOW_GAP_ADJUST = -200;

    // WEAPON CONSTANTS
    private final static int WEAPON_HIGH_SPAWN = 100;
    private final static int WEAPON_LOW_SPAWN = 500;
    private final static int WEAPON_SIDE_LEN = 32;
    private final static int WEAPON_RECT_BOUNDARY = 100;

    // GAME STATS
    private final static int LVL_ZERO_WIN_SCORE = 10;
    private final static int LVL_ONE_WIN_SCORE = 30;
    private final static int LAST_LIFE = 0;

    // GAME STATS
    private int score;
    private int animationFrameCount;
    private int flyFrameCount;
    private int flyFramesRemaining;
    private int pipeSpawnFrameCount;
    private int weaponSpawnFrameCount;
    private int shootMsgDisplayTime = SHOOT_MSG_DISPLAY_TIME;

    // GAME FLAGS
    private boolean gameStarted;
    private boolean gameOver;
    private boolean canStartLvlOne;
    private boolean scoreFlag;
    private boolean lvlZeroStart = true;
    private boolean lvlOneStart;
    private boolean addNewGaps = true;

    // GAME ENTITIES
    private final ArrayList<Pipe> pipes = new ArrayList<>();
    private final ArrayList<Weapon> weapons = new ArrayList<>();
    private final ArrayList<Integer> gaps = new ArrayList<>();
    private final Random rand = new Random();

    private Image backdrop;
    private final Font font = new Font("res/slkscr.ttf", FONT_SIZE);
    private final Bird birdPlayer = new Bird();
    private final LifeBar lifeBar = new LifeBar();

    /**
     * Initialise game.
     */
    public ShadowFlap() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, "ShadowFlap");
        backdrop = new Image("res/background.png");
        gaps.add(HIGH_GAP_ADJUST);
        gaps.add(MID_GAP_ADJUST);
        gaps.add(LOW_GAP_ADJUST);
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowFlap game = new ShadowFlap();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {

        backdrop.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // start game or make bird fly
        if (input.wasPressed(Keys.SPACE)) {
            if (detectLvlZeroWin() && canStartLvlOne) {
                lvlOneStart = true;
            }
            gameStarted = true;
            birdPlayer.setFlyingStatus(true);
            flyFramesRemaining = FLY_FRAMES;
        }

        // game over
        if (gameOver) {
            displayGameOverPrompt();
        }

        // game has not started
        if (!gameStarted) {
            displayLvlZeroInstructions();
        }

        // game is now at level one
        if (detectLvlZeroWin()) {
            lvlZeroStart = false;
            if (!lvlOneStart) {
                displayLvlOneInstructions();
                clearAllPipes();
                birdPlayer.respawn();
                lifeBar.replenishLives();
                addNewGaps();
            }
        }

        // game won
        if (detectLvlOneWin()) {
            displayWinPrompt();
        }

        // game has started
        if (gameStarted && !gameOver && !detectLvlOneWin() && (lvlZeroStart || lvlOneStart)) {

            birdPlayer.render();
            updateBird();
            spawnPipes();
            if (lvlOneStart) {
                spawnWeapons();
                updateWeapons(input);
            }
            updateTimescale(input);

            for (int i=0; i<pipes.size(); i++) {
                updateScore(i);
                updatePipe(i);
                detectCollision(i);
            }

            font.drawString(SCORE_MSG + score, SCORE_COUNTER_X_POS, SCORE_COUNTER_Y_POS);
            lifeBar.render();
        }

    }

    /**
     * Detect level zero win.
     */
    public boolean detectLvlZeroWin() {
        return score == LVL_ZERO_WIN_SCORE;
    }

    /**
     * Detect level one win.
     */
    public boolean detectLvlOneWin() {
        return score == LVL_ONE_WIN_SCORE;
    }

    /**
     * Updates bird's flapping animation, falling, and flying.
     */
    public void updateBird() {
        animationFrameCount++;
        if (animationFrameCount % FLAP_ANIM_FRAMES == 0) {
            birdPlayer.updateAnimation(lvlOneStart);
        }
        if (!birdPlayer.getFlyingStatus()) {
            birdPlayer.updateFalling(flyFrameCount);
            flyFrameCount++;
        } else {
            if (flyFramesRemaining > 0) {
                birdPlayer.updateFlying();
                flyFramesRemaining--;
            } else {
                flyFrameCount = 0;
                birdPlayer.setFlyingStatus(false);
            }
        }
    }

    /**
     * Detects updates to the timescale.
     */
    public void updateTimescale(Input input) {
        if (!pipes.isEmpty()) {
            if (input.wasPressed(Keys.L)) {
                pipes.get(0).increaseTimeScale();
            }
            if (input.wasPressed(Keys.K)) {
                pipes.get(0).decreaseTimeScale();
            }
        }
    }

    /**
     * Renders and updates the movement of a weapon.
     */
    public void updateWeapons(Input input) {
        for (int i=0; i<weapons.size(); i++) {
            weapons.get(i).render();
            weapons.get(i).updateMovement(birdPlayer, input);
            weaponHitPipe(i);
        }
    }

    /**
     * Detects collisions between fired weapons and pipes.
     */
    public void weaponHitPipe(int i) {
        for (int j=0; j<pipes.size(); j++) {
            if (pipes.get(j).getPipeRect().intersects(weapons.get(i).getWeaponLocation()) && weapons.get(i).isFired() && weapons.get(i).getRender()) {
                if (weapons.get(i).isRock()) {
                    if (!pipes.get(j).isSteel()) {
                        stopPipeRendering(j);
                    }
                } else {
                    stopPipeRendering(j);
                }
            }
        }
    }

    /**
     * Renders and updates the movement of a single pipe.
     */
    public void updatePipe(int i) {
        if (i % 2 == 0) {
            pipes.get(i).render(true);
        } else {
            pipes.get(i).render(false);
        }
        pipes.get(i).updateMovement();
    }

    /**
     * Detects if a collision has occurred.
     */
    public void detectCollision(int i) {
        // pipe or flame collision
        if (pipes.get(i).getPipeRect().intersects(birdPlayer.getLocation()) || (pipes.get(i).isSteel() && pipes.get(i).isFlameOn() && pipes.get(i).getFlameRect().intersects(birdPlayer.getLocation()))) {
            if (pipes.get(i).isRenderable()) {
                lifeBar.loseLife();
            }
            stopPipeRendering(i);
        }
        // out-of-bounds
        if (birdPlayer.getLocation().y <= 0  || birdPlayer.getLocation().y >= Window.getHeight()) {
            birdPlayer.respawn();
            lifeBar.loseLife();
        }
        // check for death
        if (lifeBar.getCurrHeart() == LAST_LIFE) {
            gameOver = true;
        }
    }

    /**
     * Updates the current score.
     */
    public void updateScore(int i) {
        if (birdPlayer.getLocation().x > pipes.get(i).getPipeRect().left() && birdPlayer.getLocation().x < pipes.get(i).getPipeRect().right()) {
            scoreFlag = true;
        }
        if (i % 2 == 0) {
            if (scoreFlag && birdPlayer.getLocation().x > pipes.get(i).getPipeRect().right() && !pipes.get(i).isPassed()) {
                score += 1;
                scoreFlag = false;
                pipes.get(i).setPassed();
            }
        }
        detectLvlOneWin();
    }

    /**
     * Spawns a new weapon provided it does not overlap the latest pipe set.
     */
    public void spawnWeapons() {
        if (weaponSpawnFrameCount % PIPE_SPAWN_RATE == 0) {

            int y = rand.nextInt(WEAPON_LOW_SPAWN-WEAPON_HIGH_SPAWN+1)+WEAPON_HIGH_SPAWN;
            Rectangle test = new Rectangle(PIPE_X_POS, y, WEAPON_SIDE_LEN+WEAPON_RECT_BOUNDARY, WEAPON_SIDE_LEN+WEAPON_RECT_BOUNDARY);

            if (!pipes.isEmpty() && !test.intersects(pipes.get(Math.max(0, pipes.size()-1)).getLocation()) && !test.intersects(pipes.get(Math.max(0, pipes.size()-2)).getLocation())) {
                if (rand.nextBoolean()) {
                    weapons.add(new Rock(PIPE_X_POS-WEAPON_RECT_BOUNDARY, y+WEAPON_RECT_BOUNDARY));
                } else {
                    weapons.add(new Bomb(PIPE_X_POS-WEAPON_RECT_BOUNDARY, y+WEAPON_RECT_BOUNDARY));
                }
            }
        }
        weaponSpawnFrameCount++;
    }

    /**
     * Main method that controls pipe spawning.
     */
    public void spawnPipes() {
        if (pipeSpawnFrameCount % PIPE_SPAWN_RATE == 0) {
            int i = gaps.get(rand.nextInt(gaps.size()));
            if (lvlOneStart) {
                spawnRandomPipes(i);
            } else {
                spawnPlasticPipes(i);
            }
        }
        pipeSpawnFrameCount++;
    }

    /**
     * Spawns either a plastic or steel pipe set.
     */
    public void spawnRandomPipes(int i) {
        if (rand.nextBoolean()) {
            spawnSteelPipes(i);
        } else {
            spawnPlasticPipes(i);
        }
    }

    /**
     * Spawns either a plastic pipe set.
     */
    public void spawnPlasticPipes(int i) {
        pipes.add(new PlasticPipe(PIPE_X_POS, -PIPE_Y_POS + i));
        pipes.add(new PlasticPipe(PIPE_X_POS, PIPE_Y_POS + i));
    }

    /**
     * Spawns either a steel pipe set.
     */
    public void spawnSteelPipes(int i) {
        pipes.add(new SteelPipe(PIPE_X_POS, -PIPE_Y_POS + i));
        pipes.add(new SteelPipe(PIPE_X_POS, PIPE_Y_POS + i));
    }

    /**
     * Clears all pipes still in the game.
     */
    public void clearAllPipes() {
        for (int i=0; i<pipes.size(); i++) {
            pipes.remove(i);
            pipes.trimToSize();
        }
    }

    /**
     * Stops rendering a pipe set.
     */
    public void stopPipeRendering(int i) {
        pipes.get(i).stopRendering();
        if (i % 2 == 0) {
            pipes.get(i+1).stopRendering();
        } else {
            pipes.get(i-1).stopRendering();
        }
    }

    /**
     * Adds new pipe gap heights for level one.
     */
    public void addNewGaps() {
        if (addNewGaps) {
            gaps.add(HIGH_MID_GAP_ADJUST);
            gaps.add(LOW_MID_GAP_ADJUST);
        }
        addNewGaps = false;
    }

    /**
     * The following methods display a corresponding game prompt.
     */
    public void displayLvlZeroInstructions() {
        font.drawString(INSTRUCTION_MSG, (Window.getWidth()/2.0 - (font.getWidth(INSTRUCTION_MSG)/2.0)), (Window.getHeight()/2.0 - (FONT_SIZE/2.0)));
    }

    public void displayLvlUpPrompt() {
        font.drawString(LVL_UP_MSG, (Window.getWidth()/2.0 - (font.getWidth(LVL_UP_MSG)/2.0)), (Window.getHeight()/2.0 - (FONT_SIZE/2.0)));
    }

    public void displayLvlOneInstructions() {
        if (shootMsgDisplayTime > 0) {
            displayLvlUpPrompt();
            shootMsgDisplayTime--;
        } else {
            canStartLvlOne = true;
            backdrop = new Image("res/level-1/background.png");
            displayLvlZeroInstructions();
            font.drawString(SHOOT_INSTRUCTION, (Window.getWidth()/2.0 - (font.getWidth(SHOOT_INSTRUCTION)/2.0)), (Window.getHeight()/2.0 - (FONT_SIZE/2.0)) + SHOOT_MSG_OFFSET);
        }
    }

    public void displayGameOverPrompt() {
        font.drawString(GAME_OVER_MSG, (Window.getWidth()/2.0 - (font.getWidth(GAME_OVER_MSG)/2.0)), (Window.getHeight()/2.0 - (FONT_SIZE/2.0)));
        font.drawString(FINAL_SCORE_MSG + score, (Window.getWidth()/2.0 - (font.getWidth(FINAL_SCORE_MSG + score)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)) + SCORE_MSG_OFFSET);
    }

    public void displayWinPrompt() {
        font.drawString(CONGRATS_MSG, (Window.getWidth()/2.0 - (font.getWidth(CONGRATS_MSG)/2.0)), (Window.getHeight()/2.0-(FONT_SIZE/2.0)));
    }

}
