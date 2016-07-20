package ca.chrisbarrett.bubblecount.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ca.chrisbarrett.bubblecount.R;
import ca.chrisbarrett.bubblecount.dao.Database;
import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.game.GameEngine;
import ca.chrisbarrett.bubblecount.util.FontCache;
import ca.chrisbarrett.bubblecount.util.PaintCache;
import ca.chrisbarrett.bubblecount.util.TextFormat;
import ca.chrisbarrett.bubblecount.util.Values;
import ca.chrisbarrett.bubblecount.view.game.model.BubbleSprite;
import ca.chrisbarrett.bubblecount.view.game.model.Sprite;

/**
 * The View for the Game, extending {@link  android.view.SurfaceView}. This View will run on a
 * separate Thread from the UI.
 * <p/>
 * <ol>
 * <li>Bubbles are randomly drawn on the Game area of the screen. The actual implementation,
 * however, maybe less if the available  screen dimensions are to hold all the Sprites.</li>
 * <li>{@link GameView#VERTICAL_DIVIDE_RATIO} defines the ratio between the Sprite area and the
 * Text area. Text area is used to hold a questionText, or statement to be displayed to the player -
 * such as "1 + 1 = ?" or "1, 2, 3, ?" </li>
 * </ol>
 *
 * @author Chris Barrett
 * @see android.view.SurfaceView;
 * @since Jun 26, 2016
 */
public class GameView extends SurfaceView implements Runnable {

    public static final float VERTICAL_DIVIDE_RATIO = 0.8f;
    public static final int SPRITE_PLACEMENT_ATTEMPTS = 5;
    public static final int MAX_SPRITES = 20; // 20; // TODO - Change back to 20 once collision works.
    public static final int MAX_ROUNDS = 5;
    private static final String TAG = "GameView";

    private Thread gameThread = null;

    private Context context;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint textPaint;
    private Paint drawPaint;

    private float screenWidth;
    private float gameAreaHeight;
    private float textAreaHeight;
    private int backgroundColor;

    private long gameId;
    private GameEngine gameEngine;
    private String gameEngineQuestion;
    private String gameEngineAnswer;
    private OnGameViewListener gameListener;

    private long roundStartTime;
    private long totalTime;
    private volatile boolean isRunning;
    private volatile boolean isNewRound;
    private int roundCounter;
    private Set<Sprite> sprites;

    /**
     * Default constructor when inflated programmatically
     *
     * @param context Context on which the GameView is displayed
     */
    public GameView (Context context) {
        this(context, null);
    }


    /**
     * Default constructor when inflated from XML file
     *
     * @param context Context on which the GameView is displayed
     * @param attrs   optional attributes provided by the XML file
     */
    public GameView (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor when inflated from XML file in API 11+
     *
     * @param context  Context on which the GameView is displayed
     * @param attrs    optional attributes provided by the XML file
     * @param defStyle optional defined theme styles provided by XML file
     */
    public GameView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "Instantiating GameView");
        surfaceHolder = getHolder();
        this.context = context;
        try {
            gameListener = (OnGameViewListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameViewListener");
        }
        backgroundColor = ContextCompat.getColor(context, R.color.primaryBackground);
    }

    //
    // Life cycle events begin here
    //

    /**
     * Must be called when the calling Activity or Fragment calls onResume. Method checks the
     * screen dimensions and starts a new thread to run the game.
     */
    public void onResume () {
        Log.d(TAG, "GameView onResume called");
        isRunning = true;
        isNewRound = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Must be called when the calling Activity or Fragment calls onPause. Method shuts down the
     * game thread.
     */
    public void onPause () {
        Log.d(TAG, "GameView onPause called");
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Thread runner method. Prepares the game for display, records the start time of the game,
     * then loops as the game is running.
     */
    @Override
    public void run () {
        Log.d(TAG, "Setting up the game...");
        // load the game engine
        gameEngine = loadGame();

        // setup the painters
        textPaint = PaintCache.getTextPainter();
        textPaint.setTypeface(FontCache.getFont(context));
        drawPaint = PaintCache.getDrawablePainter();

        // determine the screen dimensions and define the two areas
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        float screenHeight = metrics.heightPixels;
        gameAreaHeight = screenHeight * VERTICAL_DIVIDE_RATIO;
        textAreaHeight = screenHeight - gameAreaHeight;

        roundCounter = 1;
        // looper to run the game on the thread
        while (isRunning) {
            if (isNewRound) {
                Log.d(TAG, "Setting up round: " + roundCounter);
                prepareRound();
                roundCounter++;
                isNewRound = false;
                roundStartTime = System.currentTimeMillis();
            }
            update();
            draw();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread failed...");
            }
        }
    }

    //
    // Helper methods begin here
    //

    /**
     * Monitors the screen for touches. When the screen is touched:
     * <ul>
     * <li>Triggering the correctAnswer will end the game running and fire the end game routine.</li>
     * <li>Triggering the incorrectAnswer will make that sprite invisible</li>
     * </ul>
     * <p/>
     * <p/>
     * The method checks to see if the correct answer has been hit. If the correct answer has been
     * hit, a check of the game state will be done.
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent (MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean isGameFinished = false;
                float x = event.getX();
                float y = event.getY();
                Iterator<Sprite> spriteIterator = sprites.iterator();
                while (spriteIterator.hasNext()) {
                    Sprite sprite = spriteIterator.next();
                    if (sprite.isCollision(x, y)) {
                        if (gameEngineAnswer.equals(sprite.getText())) {
                            checkGameState();
                            break;
                        } else {
                            sprite.setVisibility(false);
                        }
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * This method updates the drawables. As positions inside the Set are updated, an Iterator is used.
     * Generally, after updating, {@link #draw()} should be called.
     */
    protected void update () {
        Iterator<Sprite> spriteIterator = sprites.iterator();

        while (spriteIterator.hasNext()) {
            Sprite outerSprite = spriteIterator.next();
            outerSprite.update();
            // deletes any sprites that have been turned invisible and skips if this sprite has been updated.
            if (!outerSprite.getVisibility()) {
                spriteIterator.remove();
                continue;
            }
            outerSprite.animate();
        }
    }

    /**
     * Draws the drawables. For sprites, a foreach loop is used as no changes to the sprite values
     * takes place. This method should be called after {@link #update()}.
     */
    protected void draw () {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(backgroundColor);
            drawPaint.setColor(ContextCompat.getColor(context, R.color.secondaryBackground));
            canvas.drawRect(0, gameAreaHeight, screenWidth, gameAreaHeight + textAreaHeight, drawPaint);
            drawPaint.setColor(ContextCompat.getColor(context, R.color.divider));
            canvas.drawRect(0, gameAreaHeight, screenWidth, gameAreaHeight + 5, drawPaint);
            canvas.drawRect(gameAreaHeight, screenWidth-5, screenWidth, gameAreaHeight + textAreaHeight, drawPaint);
            textPaint.setColor(ContextCompat.getColor(context, R.color.primaryDark));
            canvas.drawText(gameEngineQuestion, screenWidth / 2f, TextFormat
                    .verticalCenter(gameAreaHeight, textAreaHeight + gameAreaHeight,
                            textPaint), textPaint);
            for (Sprite sprite : sprites) {
                sprite.draw(canvas, drawPaint, textPaint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    /**
     * This method loads the {@link GameEngine} from the database based on the preference setting.
     * If the preference is null, or the preference is equal to 1, then a random game will be loaded.
     * We do it in this class, as it's closely related to the GameView and the GameView can run this
     * on another thread.
     *
     * @return
     */
    protected GameEngine loadGame () {
        Log.d(TAG, "Attempting to load GameEngine.");
        Game game = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        long defaultGameId = getResources().getInteger(R.integer.pref_game_selector_default_value);
        long gameSelector = Long.valueOf(sharedPreferences.getString(getResources().getString(R.string.pref_game_selector_key), "" + defaultGameId));
        Log.d(TAG, "Retrieved GameEngine id: " + gameSelector);
        try {
            Database db = new Database(getContext());
            db.open();
            if (Game.RANDOM == gameSelector) {
                List<Game> games = db.gameDao.selectAllGames();
                Collections.shuffle(games, Values.RANDOM);
                game = games.get(0);
                Log.d(TAG, "Randomly selected: " + game.toString());
            } else {
                game = db.gameDao.selectGameById(gameSelector);
                Log.d(TAG, "As per User, selected: " + game.toString());
            }
            db.close();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        gameId = game.getId();
        GameEngine loadedEngine = null;
        try {
            Log.d(TAG, "Attempting to instantiate using: " + game.getClassPathName());
            ClassLoader classLoader = this.getClass().getClassLoader();
            loadedEngine = (GameEngine) classLoader.loadClass(game.getClassPathName()).newInstance();
            Log.d(TAG, "Instantiated: " + loadedEngine.getClass());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        return loadedEngine;
    }

    /**
     * Sets up the round in the background. Positions the Sprites randomly in the defined area for
     * Sprites.
     * <p/>
     * Brute force is used to make sure the Sprites do not overlap. {@value GameView#SPRITE_PLACEMENT_ATTEMPTS}
     * attempts to  place the Sprite without overlap will be tried. By keeping the attempts low,
     * the perception of a random generation in numbers is achieved.
     * <p/>
     * The total number of Sprites will be the lesser of the size of {@link GameEngine#getCorrectElement()}
     * and {@link #MAX_SPRITES}
     */
    protected void prepareRound () {
        gameEngine.randomize();
        gameEngineAnswer = gameEngine.getCorrectElement();
        gameEngineQuestion = gameEngine.getQuestion();
        sprites = new HashSet();

        // Always insert the first bubble_sprite as it as the correct answer
        sprites.add(new BubbleSprite(context, screenWidth, gameAreaHeight, gameEngineAnswer));

        boolean isSpriteOverlap;
        Iterator<String> iteratorElements = gameEngine.getIncorrectElements().iterator();
        while (iteratorElements.hasNext() && sprites.size() < MAX_SPRITES) {
            Sprite newSprite;
            int creationAttempt = 0;
            do {
                isSpriteOverlap = false;
                newSprite = new BubbleSprite(context, screenWidth, gameAreaHeight, null);
                for (Sprite sprite : sprites) {
                    if (sprite.isCollision(newSprite)) {
                        isSpriteOverlap = true;
                        creationAttempt++;
                    }
                }
            } while (isSpriteOverlap && creationAttempt < SPRITE_PLACEMENT_ATTEMPTS);
            if (creationAttempt < SPRITE_PLACEMENT_ATTEMPTS) {
                newSprite.setText(iteratorElements.next());
                sprites.add(newSprite);
            }
        }
        Log.d(TAG, "Preparing Round is complete.");
    }


    /**
     * Helper method to check the state of the game. IF the roundCounter hasn't yet hit {@link #MAX_ROUNDS},
     * then a new round will begin. Otherwise, the game will shutdown and return to the calling
     * Activity the totalTime.
     */
    protected void checkGameState () {
        totalTime = totalTime + (System.currentTimeMillis() - roundStartTime);
        if (roundCounter <= MAX_ROUNDS) {
            isNewRound = true;
        } else {
            Log.d(TAG, "Game End called. Shutting down...");
            saveGameResult();
            gameListener.onGameEnd(gameId, totalTime);
            isRunning = false;
        }
    }

    /**
     * Helper method saves
     */
    protected void saveGameResult () {

    }

    /**
     * Activities and Fragments are required to implement the OnGameViewListener.
     * Game context material is available through the listener.
     */
    public interface OnGameViewListener {

        /**
         * onGameEnd is called when a game has ended
         *
         * @param gameId the gameId of the game played
         * @param time   the total time in milliseconds that the game took
         */
        void onGameEnd (long gameId, long time);
    }
}

