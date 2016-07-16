package ca.chrisbarrett.bubblecount;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.chrisbarrett.bubblecount.dao.AppDatabaseHelper;
import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.dao.model.Player;
import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;

/**
 * The Parental Control area of the app. Parents will have the ability to set global options,
 * such as sound availability, configure Player and Data synch options
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements BackgroundMusicManager
        .OnBackgroundMusicListener {

    private static final String TAG = "SettingsActivity";
    private static final BackgroundMusicManager MUSIC_MANAGER = BackgroundMusicManager.getInstance();
    private static final String ARRAY_GAME_TITLES = "ARRAY_GAME_TITLES";
    private static final String ARRAY_GAME_VALUES = "ARRAY_GAME_VALUES";
    private static final String ARRAY_PLAYER_TITLES = "ARRAY_PLAYER_TITLES";
    private static final String ARRAY_PLAYER_VALUES = "ARRAY_PLAYER_VALUES";

    private static boolean isContinueMusic;
    private boolean isMusicOn;
    private static Map<String, String[]> games;
    private static Map<String, String[]> players;

    //
    // LifeCycles Events Begin Here for the SettingsActivity
    //

    /**
     * Similar to other Activities, the BackgroundMusicManager must be bound and the music loaded
     * if required. In addition, the view is replaced initially with the GeneralSettingsFragment
     * and the database is polled for Players
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralSettingsFragment()).commit();
        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetGamesTask().execute();
        new GetPlayersTask().execute();
        MUSIC_MANAGER.initialize(this, R.raw.background);
        isMusicOn = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(getString(R.string.pref_music_is_on_key),
                        getResources().getBoolean(R.bool.pref_music_is_on_default));
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

    /**
     * Makes sure triggering the back button does not accidentally shut down the music if playing
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed. Making sure isContinueMusic continues.");
        isContinueMusic = true;
        super.onBackPressed();
    }

    //
    // Listeners begin here
    //

    /**
     * Starts the music when the MUSIC_MANAGER advises the music is ready
     */
    @Override
    public void onMusicReady() {
        if (isMusicOn && !MUSIC_MANAGER.isPlaying()) {
            Log.d(TAG, "Calling for music to start.");
            MUSIC_MANAGER.musicStart();
        }
    }

    /**
     * This listener monitors the PreferenceChanges and fires the appropriate response.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof GameListPreference) {
                Log.d(TAG, "OnPreferenceChangeListener heard change in GameListPreference");


            } else if (preference instanceof PlayerDialogPreference) {
                Log.d(TAG, "OnPreferenceChangeListener heard change in PlayerDialogPreference");

            } else if (preference instanceof ListPreference) {
                String stringValue = value.toString();
                Log.d(TAG, "OnPreferenceChangeListener heard change in ListPreference");

                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(value.toString());
            }
            return true;
        }
    };

    //
    // Helper methods begin here
    //

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     * <p/>
     * This binder should be used in any fragment that needs to bind  EditText/List/Dialog/Ringtone
     * preferences to  their values. When  their  values change, their summaries are updated to
     * reflect the new value, per the Android Design guidelines.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralSettingsFragment.class.getName().equals(fragmentName);
    }

    //
    // Inner fragment classes begin here
    //

    /**
     * Inner class for displaying the general control settings. This is the landing page fragment
     * that appears first. This fragment also has an option to toggle music, which is controlled
     * by the {@link android.preference.Preference.OnPreferenceClickListener}
     */
    public static class GeneralSettingsFragment extends PreferenceFragment {

        private static final String TAG = "GeneralSettingsFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_fragment_general);
            setHasOptionsMenu(true);
            findPreference(getString(R.string.pref_music_is_on_key)).setOnPreferenceClickListener
                    (new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            if (MUSIC_MANAGER.isReady()) {
                                if (MUSIC_MANAGER.isPlaying()) {
                                    MUSIC_MANAGER.musicPause();
                                } else {
                                    MUSIC_MANAGER.musicStart();
                                }
                            }
                            return true;
                        }
                    });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.d(TAG, "Menu pressed from GeneralSettingsFragment.");
            switch (item.getItemId()) {
                case android.R.id.home:
                    Log.d(TAG, "Going home...");
                    isContinueMusic = true;
                    getActivity().setResult(RESULT_OK);
                    getActivity().finish();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * GameListPreference is used to display a custom ListPreference containing a list of games
     * referenced to the game's primary ID in the database.
     */
    public static class GameListPreference extends ListPreference {

        public GameListPreference(Context context) {
            super(context, null);
        }

        public GameListPreference(Context context, AttributeSet attrs) {
            super(context, attrs);

            //bindPreferenceSummaryToValue("");
        }


    }

    /**
     * This class is a Custom DialogPreference used to edit player details
     */
    public static class PlayerDialogPreference extends DialogPreference {

        private static final String TAG = "PlayerDialogPreference";
        private Player player;
        private TextView tvTitle;
        private EditText etName;
        private YearPicker yearPicker;

        public PlayerDialogPreference(Context context, AttributeSet attrs) {
            super(context, attrs);
            setDialogLayoutResource(R.layout.preference_player);
            setPersistent(false);
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                Log.d(TAG, attrs.getAttributeName(i) + ":" + attrs.getAttributeValue(i));
            }
        }

        /**
         * This class provides a custom year picker for the PlayerFeed
         */
        public static class YearPicker extends NumberPicker {

            public YearPicker(Context context, AttributeSet attrs) {
                super(context, attrs);
            }


        }
    }


    /**
     * This inner class retrieves from the database a Map containing two String arrays:
     * <ul>
     * <li>Key: {@link SettingsActivity#ARRAY_GAME_TITLES}, Values: A List of Strings containing
     * the displayable names of all Games</li>
     * <li>Key: {@link SettingsActivity#ARRAY_GAME_VALUES}, Values: A List of Strings containing
     * the primary keys corresponding to the Games</li>
     * </ul>
     */
    private class GetGamesTask extends AsyncTask<Void, Void, Map<String, String[]>> {

        @Override
        protected Map<String, String[]> doInBackground(Void... params) {
            Log.d(TAG, "Attempting to retrieve Games from database.");
            AppDatabaseHelper dbHelper = new AppDatabaseHelper(getApplicationContext());
            SQLiteDatabase db = null;
            Map<String, String[]> dbResults = new HashMap<>();
            try {
                List<String> dbValues = new ArrayList<>();
                List<String> dbTitles = new ArrayList<>();
                db = dbHelper.getReadableDatabase();
                List<Game> dbGames = dbHelper.getAllGames(db);
                for (Game dbGame : dbGames) {
                    dbValues.add(String.valueOf(dbGame.getId()));
                    dbTitles.add(dbGame.getDisplayName());
                    Log.d(TAG, dbGame.getId() + " : " + dbGame.getDisplayName());
                }
                dbResults.put(SettingsActivity.ARRAY_GAME_TITLES, dbTitles.toArray(new String[dbTitles.size()]));
                dbResults.put(SettingsActivity.ARRAY_GAME_VALUES, dbValues.toArray(new String[dbValues.size()]));
            } catch (SQLException e) {
                Log.e(TAG, "Attempting to GetGamesTask caused: " + e.getMessage());
            } finally {
                if (db != null) {
                    db.close();
                }
            }
            return dbResults;
        }

        @Override
        protected void onPostExecute(Map<String, String[]> stringMap) {
            Log.d(TAG, "Completed retrieval of Games.");
            super.onPostExecute(stringMap);
            games = stringMap;
        }
    }

    /**
     * This inner class retrieves from the database a Map containing two String arrays:
     * <ul>
     * <li>Key: {@link SettingsActivity#ARRAY_PLAYER_TITLES}, Values: A List of Strings containing
     * the displayable names of all Players</li>
     * <li>Key: {@link SettingsActivity#ARRAY_PLAYER_VALUES}, Values: A List of Strings containing
     * the primary keys corresponding to the Players</li>
     * </ul>
     */
    private class GetPlayersTask extends AsyncTask<Void, Void, Map<String, String[]>> {

        @Override
        protected Map<String, String[]> doInBackground(Void... params) {
            Log.d(TAG, "Attempting to retrieve Players from database.");
            AppDatabaseHelper dbHelper = new AppDatabaseHelper(getApplicationContext());
            SQLiteDatabase db = null;
            Map<String, String[]> dbResults = new HashMap<>();
            try {
                List<String> dbValues = new ArrayList<>();
                List<String> dbTitles = new ArrayList<>();
                db = dbHelper.getReadableDatabase();
                List<Player> dpPlayers = dbHelper.getAllPlayers(db);
                for (Player dbPlayer : dpPlayers) {
                    dbValues.add(String.valueOf(dbPlayer.getId()));
                    dbTitles.add(dbPlayer.getName());
                    Log.d(TAG, dbPlayer.getId() + " : " + dbPlayer.getName());
                }
                dbResults.put(SettingsActivity.ARRAY_PLAYER_TITLES, dbTitles.toArray(new String[dbTitles.size()]));
                dbResults.put(SettingsActivity.ARRAY_PLAYER_VALUES, dbValues.toArray(new String[dbValues.size()]));
            } catch (SQLException e) {
                Log.e(TAG, "Attempting to GetPlayersTask caused: " + e.getMessage());
            } finally {
                if (db != null) {
                    db.close();
                }
            }
            return dbResults;
        }

        @Override
        protected void onPostExecute(Map<String, String[]> stringMap) {
            Log.d(TAG, "Completed retrieval of Players.");
            super.onPostExecute(stringMap);
            players = stringMap;
        }
    }
}
