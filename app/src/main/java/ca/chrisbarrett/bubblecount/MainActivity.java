package ca.chrisbarrett.bubblecount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import ca.chrisbarrett.bubblecount.dao.AppDatabaseHelper;
import ca.chrisbarrett.bubblecount.dao.model.GameResult;
import ca.chrisbarrett.bubblecount.dao.model.Player;
import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;
import ca.chrisbarrett.bubblecount.util.Values;

/**
 * The main entry point for the app. Presents an activity with three options:
 * <ol>
 * <li>Play GameFeed - Starts the GameFeed</li>
 * <li>Settings - User Configuration</li>
 * <li>Music Toggle - Music On/Off (Visibility can be overridden in Settings by settings global
 * Music Toggle Off</li>
 * </ol>
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        BackgroundMusicManager.OnBackgroundMusicListener {

    private static final String TAG = "MainActivity";

    private static final BackgroundMusicManager MUSIC_MANAGER = BackgroundMusicManager.getInstance();
    private static boolean isContinueMusic;
    private ToggleButton toggleMusic;
    private boolean isMusicToggleAvailable;
    private boolean isMusicOn;


    //
    // LifeCycles Events Begin Here
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
        Button buttonGamePlay = (Button) findViewById(R.id.button_main_game);
        if (buttonGamePlay != null) {
            buttonGamePlay.setOnClickListener(this);
        }
        Button buttonSettings = (Button) findViewById(R.id.button_main_settings);
        if (buttonSettings != null) {
            buttonSettings.setOnClickListener(this);
        }
        toggleMusic = (ToggleButton) findViewById(R.id.togglebutton_main_music);

        testDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called.");
        // Loading the music can take time. Do this as soon as you can.
        MUSIC_MANAGER.initialize(this, R.raw.background);
        loadRelevantPreferences();
        if (isMusicToggleAvailable) {
            toggleMusic.setChecked(isMusicOn);
            toggleMusic.setOnClickListener(this);
            toggleMusic.setVisibility(View.VISIBLE);
        } else {
            toggleMusic.setVisibility(View.GONE);
        }
        isContinueMusic = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isContinueMusic) {
            Log.d(TAG, "onPause called and releasing MUSIC_MANAGER.");
            MUSIC_MANAGER.musicRelease();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Values.ResultCode.ACTIVITY_SETTINGS:
                Log.d(TAG, "Back from SettingsActivity");
                // Nothing to do here - placeholder for now
                break;
            case Values.ResultCode.ACTIVITY_GAME:
                Log.d(TAG, "Back from GameActivity");
                if (RESULT_OK == resultCode) {
                    Log.d(TAG, "Back from GamesActivity");
                    // Nothing to do here - placeholder for now
                } else if (RESULT_CANCELED == resultCode) {
                    // PlayerFeed didn't finish a game - nothing to do here
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    // Listeners Begin Here
    //

    /**
     * Three events are handled in the onClick:
     * <ul>
     * <li>GameFeed Button pressed - musicStart the game</li>
     * <li>Settings Button pressed - musicStart the settings</li>
     * <li>Music Toggle - turned music off and on (if the option is available)</li>
     * </ul>
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_main_game:
                Log.d(TAG, "GameFeed Button pressed");
                intent = new Intent(this, GameActivity.class);
                isContinueMusic = true;
                startActivityForResult(intent, Values.ResultCode.ACTIVITY_GAME);
                break;
            case R.id.button_main_settings:
                Log.d(TAG, "Settings Button pressed");
                intent = new Intent(this, SettingsActivity.class);
                isContinueMusic = true;
                startActivityForResult(intent, Values.ResultCode.ACTIVITY_SETTINGS);
                break;
            case R.id.togglebutton_main_music:
                isMusicOn = toggleMusic.isChecked();
                Log.d(TAG, "Music Toggle changed to " + isMusicOn);
                if (isMusicOn) {
                    MUSIC_MANAGER.musicStart();
                } else {
                    MUSIC_MANAGER.musicPause();
                }
                saveMusicOnPreference();
                break;
        }
    }

    /**
     * Starts the music when the MUSIC_MANAGER advises the music is ready
     */
    @Override
    public void onMusicReady() {
        Log.d(TAG, "Notified by BackgroundMusicManager music is ready.");
        if (isMusicOn && !MUSIC_MANAGER.isPlaying()) {
            Log.d(TAG, "Calling for music to start.");
            MUSIC_MANAGER.musicStart();
        }
    }

    //
    // Helper methods begin here
    //

    /**
     * This method loads the preferences relevant to this task, using property files for the
     * defaults
     */
    protected void loadRelevantPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isMusicToggleDefault = getResources().getBoolean(R.bool.pref_music_toggle_available_default);
        isMusicToggleAvailable = sharedPref.getBoolean(getString(R.string.pref_music_toggle_available_key),
                isMusicToggleDefault);
        isMusicOn = sharedPref.getBoolean(getString(R.string.pref_music_is_on_key), isMusicToggleAvailable);
    }

    /**
     * This method updates the preference for isMusicOn
     */
    protected void saveMusicOnPreference() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(getString(R.string.pref_music_is_on_key), isMusicOn).commit();
        Log.d(TAG, String.format("Saved Preference for MusicOn to: %b",
                sharedPref.getBoolean(getString(R.string.pref_music_is_on_key), false)));
    }


    void testDatabase() {
        Log.d(TAG, "Testing player insertion");
        Player player = new Player("Chris", 1974);
        AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.insertPlayer(db, player);
        GameResult gameResult = new GameResult(1000, 1, 1);
        databaseHelper.insertGameResult(db,gameResult);


    }
}