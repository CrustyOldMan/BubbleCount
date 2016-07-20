package ca.chrisbarrett.bubblecount;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import ca.chrisbarrett.bubblecount.dao.Database;
import ca.chrisbarrett.bubblecount.dao.model.GameResult;
import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;
import ca.chrisbarrett.bubblecount.util.GlobalContext;
import ca.chrisbarrett.bubblecount.view.GameView;

/**
 * A helper Activity that starts and stops the {@link ca.chrisbarrett.bubblecount.view.GameView;}
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class GameActivity extends AppCompatActivity implements GameView.OnGameViewListener,
        BackgroundMusicManager.OnBackgroundMusicListener {

    private static final String TAG = "GameActivity";
    private static final BackgroundMusicManager MUSIC_MANAGER = BackgroundMusicManager.getInstance();

    private static boolean isContinueMusic;
    private boolean isMusicOn;
    private GameView gameView;

    //
    // LifeCycles Events Begin Here
    //

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        GlobalContext gc = GlobalContext.getInstance();
        gc.initialize(getApplicationContext());
        setContentView(gameView);
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
    }

    @Override
    protected void onResume () {
        super.onResume();
        isContinueMusic = false;
        gameView.onResume();
        MUSIC_MANAGER.initialize(this, R.raw.background);
        isMusicOn = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(getString(R.string.pref_music_is_on_key),
                        getResources().getBoolean(R.bool.pref_music_is_on_default));

    }

    @Override
    protected void onPause () {
        super.onPause();
        gameView.onPause();
        if (!isContinueMusic) {
            Log.i(TAG, "onPause called and releasing MUSIC_MANAGER.");
            MUSIC_MANAGER.musicRelease();
        }
    }

    /**
     * Makes sure triggering the back button does not accidentally shut down the music if playing
     */
    @Override
    public void onBackPressed () {
        Log.d(TAG, "Back button pressed. Making sure isContinueMusic continues.");
        isContinueMusic = true;
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    //
    // Listeners begin here
    //

    /**
     * Starts the music when the MUSIC_MANAGER advises the music is ready
     */
    @Override
    public void onMusicReady () {
        if (isMusicOn && !MUSIC_MANAGER.isPlaying()) {
            Log.d(TAG, "Calling for music to start.");
            MUSIC_MANAGER.musicStart();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameEnd (long gameId, long time) {
        Log.d(TAG, "Received notification game is done. Total time: " + time);
        Toast.makeText(this, String.format("Total time: %d seconds", time / 1000), Toast.LENGTH_SHORT).show();
        new SaveTimeToDatabase().execute(gameId, time);
        isContinueMusic = true;
        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * This AsyncTask inner class saves the GameResult to the database
     */
    protected class SaveTimeToDatabase extends AsyncTask<Long, Void, Boolean> {

        Context context;

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            context = getBaseContext();
        }

        @Override
        protected Boolean doInBackground (Long... params) {
            long gameId = params[0];
            long time = params[1];
            Log.d(TAG, "doInBackground called with time: " + time);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            long defaultPlayerId = getResources().getInteger(R.integer.pref_player_selector_default_value);
            long playerId = Long.valueOf(sharedPreferences.getString(getResources().getString(R.string.pref_player_selector_key), "" + defaultPlayerId));
            GameResult gameResult = new GameResult(time, playerId, gameId);
            Log.d(TAG, "Attempting to save: "+gameResult.toString());
            Database db = new Database(context);
            try {
                db.open();
                boolean result = db.gameResultDao.insertGameResult(gameResult);
                Log.d(TAG, String.format("GameResult was%ssuccessfully inserted.", result ? " " : " not "));
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                db.close();
            }
            return true;
        }

        @Override
        protected void onPostExecute (Boolean result) {
            super.onPostExecute(result);
            Toast.makeText(context,
                    result ? "Results Saved" : "Database Unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }
}