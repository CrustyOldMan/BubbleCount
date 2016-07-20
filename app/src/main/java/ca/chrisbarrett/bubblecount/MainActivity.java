package ca.chrisbarrett.bubblecount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.Arrays;

import ca.chrisbarrett.bubblecount.dao.Database;
import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;
import ca.chrisbarrett.bubblecount.util.GlobalContext;
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

    private Database db;
    private ToggleButton toggleMusic;
    private boolean isMusicToggleAvailable;
    private boolean isMusicOn;
    private long gameId;
    private long playerId;
    private AdapterView.OnItemClickListener dialogListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

        }
    };

    //
    // LifeCycles Events Begin Here
    //

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        GlobalContext gc = GlobalContext.getInstance();
        gc.initialize(getApplicationContext());
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
        ((Button) findViewById(R.id.button_main_game)).setOnClickListener(this);
        ((Button) findViewById(R.id.button_main_settings)).setOnClickListener(this);
        toggleMusic = (ToggleButton) findViewById(R.id.togglebutton_main_music);
        checkPreferences();
    }

    @Override
    protected void onResume () {
        super.onResume();
        Log.d(TAG, "onResume called.");
        // Loading the music can take time. Do this as soon as you can.
        MUSIC_MANAGER.initialize(this, R.raw.background);
        db = new Database(this).open();
        getPreferences();
        if (isMusicToggleAvailable) {
            Log.d(TAG, "Music Toggle is set to on.");
            toggleMusic.setChecked(isMusicOn);
            toggleMusic.setOnClickListener(this);
            toggleMusic.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Music Toggle is set to off.");
            toggleMusic.setVisibility(View.GONE);
        }
        isContinueMusic = false;
    }

    @Override
    protected void onPause () {
        super.onPause();
        Log.d(TAG, "onPause called.");
        if (!isContinueMusic) {
            Log.d(TAG, "Releasing MUSIC_MANAGER.");
            MUSIC_MANAGER.musicRelease();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        Log.d(TAG, "onDestroy called.");
        db.close();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d(TAG, String.format("onActivityResult called: request: %d, result: %d", requestCode, resultCode));
        switch (requestCode) {
            case Values.ResultRequest.ACTIVITY_SETTINGS:
                Log.d(TAG, "Back from SettingsActivity");
                if (RESULT_OK == resultCode) {
                    Log.d(TAG, "Things are OK");
                    // Nothing to do here - placeholder for now
                } else if (RESULT_CANCELED == resultCode) {
                    Log.d(TAG, "User seems to have canceled...");
                    // Nothing to do here - placeholder for now
                }
                break;
            case Values.ResultRequest.ACTIVITY_GAME:
                Log.d(TAG, "Back from GameActivity");
                if (RESULT_OK == resultCode) {
                    Log.d(TAG, "Things are OK");
                    // Nothing to do here - placeholder for now
                } else if (RESULT_CANCELED == resultCode) {
                    Log.d(TAG, "User seems to have canceled...");
                    // Nothing to do here - placeholder for now
                }
                break;
            default:
                Log.e(TAG, "onActivity() received unknown requestCode: " + requestCode);
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
    public void onClick (View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_main_game:
                Log.d(TAG, "GameFeed Button pressed");
                intent = new Intent(this, GameActivity.class);
                intent.putExtra(Values.Extra.GAME_ID_SELECTOR, gameId);
                intent.putExtra(Values.Extra.PLAYER_ID_SELECTOR, playerId);
                isContinueMusic = true;
                startActivityForResult(intent, Values.ResultRequest.ACTIVITY_GAME);
                break;
            case R.id.button_main_settings:
                Log.d(TAG, "Settings Button pressed");
                intent = new Intent(this, SettingsActivity.class);
                isContinueMusic = true;
                startActivityForResult(intent, Values.ResultRequest.ACTIVITY_SETTINGS);
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
    public void onMusicReady () {
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
     * This method starts the GameActivity
     *
     * @param intent
     */
    protected void startGameActivity (Intent intent) {


    }

    /**
     * This method updates the preference for isMusicOn
     */
    protected void saveMusicOnPreference () {
        Log.d(TAG, String.format("saveMusicOnPreference - saving MusicOn to: %b.", isMusicOn));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(getString(R.string.pref_music_is_on_key), isMusicOn).apply();
    }

    /**
     * This method loads the preferences needed for this Activity
     */
    protected void getPreferences () {
        Log.d(TAG, "getPreferences called.");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        isMusicToggleAvailable = sharedPref.getBoolean(
                getString(R.string.pref_music_toggle_available_key),
                getResources().getBoolean(R.bool.pref_music_toggle_available_default));
        isMusicOn = sharedPref.getBoolean(
                getString(R.string.pref_music_is_on_key), isMusicToggleAvailable);
        gameId = Long.valueOf(
                sharedPref.getString(getString(R.string.pref_game_selector_key),
                        String.valueOf(getResources().getInteger(R.integer.pref_game_selector_default_value))));
        playerId = Long.valueOf(
                sharedPref.getString(getString(R.string.pref_player_selector_key),
                        String.valueOf(getResources().getInteger(R.integer.pref_player_selector_default_value))));
    }


    // Sanity check to make sure preferences are saving
    private void checkPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, Arrays.toString(sharedPref.getAll().entrySet().toArray()));
    }
}