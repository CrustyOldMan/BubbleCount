package ca.chrisbarrett.bubblecount;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import ca.chrisbarrett.bubblecount.dao.Database;
import ca.chrisbarrett.bubblecount.dao.GameDao;
import ca.chrisbarrett.bubblecount.dao.PlayerDao;
import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.dialog.ListDialogFragment;
import ca.chrisbarrett.bubblecount.preference.PlayerDialogPreference;
import ca.chrisbarrett.bubblecount.service.BackgroundMusicManager;

/**
 * The Parental Control area of the app. Parents will have the ability to set global options,
 * such as sound availability, configure Player and Data synch options
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements
        BackgroundMusicManager.OnBackgroundMusicListener, ListDialogFragment.OnListDialogItemClickListener {

    private static final String TAG = "SettingsActivity";

    // args used to management ListFragment callbacks
    private static final String LIST_CALLER_ID = "LIST_CALLER_ID";
    private static final int SET_DEFAULT_GAME = 1;
    private static final int SET_DEFAULT_PLAYER = 2;
    private static final int DELETE_PLAYER = 3;
    private static final int EDIT_PLAYER = 4;

    private static final BackgroundMusicManager MUSIC_MANAGER = BackgroundMusicManager.getInstance();
    private static boolean isContinueMusic;
    private static Database db;

    /**
     * This listener monitors the PreferenceChanges and fires the appropriate responses.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange (Preference preference, Object value) {
            Log.d(TAG, "onPreferenceChange called.");

            String stringValue = value.toString();
            String preferenceKey = preference.getKey();
            Context context = preference.getContext();

            if (preference instanceof ListPreference) {
                Log.d(TAG, "OnPreferenceChangeListener heard change in ListPreference.");

                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }

            // Sets the display value to match the view
            else if (preferenceKey.equals(context.getString(R.string.pref_game_selector_key))) {
                Log.d(TAG, "OnPreferenceChangeListener heard change in pref_game_selector_key.");

                if (stringValue == null || stringValue.trim().isEmpty()) {
                    stringValue = "" + 1;
                }
                Game game = null;
                try {
                      game = db.gameDao.selectGameById(Long.valueOf(stringValue));
                } catch (SQLException  e ) {
                    Log.e(TAG, e.getMessage());
                }
                if (game != null) {
                    preference.setSummary(game.getDisplayName());

                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return false;
        }
    };
    private boolean isMusicOn;
    private PlayerDialogPreference.OnPlayerDialogPreferenceListener playerListener = new PlayerDialogPreference.OnPlayerDialogPreferenceListener() {
        @Override
        public void onPlayerUpdate (Bundle result) {
            Log.d(TAG, String.format("Name %s : Year: %d", result.getString("name"), result.getInt("year")));
        }
    };

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
    private static void bindPreferenceSummaryToValue (Preference preference) {
        Log.d(TAG, "bindPreferenceSummaryToValue assigned to: " + preference.toString());

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMusicReady () {
        if (isMusicOn && !MUSIC_MANAGER.isPlaying()) {
            Log.d(TAG, "Calling for music to start.");
            MUSIC_MANAGER.musicStart();
        }
    }

    @Override
    public void OnListDialogItemClick (int callerId, int position, long id) {
        Log.d(TAG, String.format("User updated to position %d, id %d using caller: ", position, id, callerId));
    }

    /**
     * Similar to other Activities, the BackgroundMusicManager must be bound and the music loaded
     * if required. In addition, the view is replaced initially with the GeneralPreferenceFragment
     * and the database is polled for Players
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called.");
        MUSIC_MANAGER.setOnBackgroundMusicListener(this);
        db = new Database(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
        setupActionBar();
    }

    @Override
    protected void onResume () {
        super.onResume();
        Log.d(TAG, "onResume called.");
        MUSIC_MANAGER.initialize(this, R.raw.background);
        db.open();
        isMusicOn = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(getString(R.string.pref_music_is_on_key), getResources().getBoolean(R.bool.pref_music_is_on_default));
        isContinueMusic = false;
    }

    @Override
    protected void onPause () {
        super.onPause();
        Log.d(TAG, "onResume called.");
        if (!isContinueMusic) {
            Log.d(TAG, "Now releasing MUSIC_MANAGER.");
            MUSIC_MANAGER.musicRelease();
        }
    }

    @Override
    protected void onDestroy () {
        Log.d(TAG, "onDestroy called.");
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed () {
        Log.d(TAG, "Back button pressed. Making sure isContinueMusic continues.");
        isContinueMusic = true;
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    protected void setupActionBar () {
        Log.d(TAG, "setupActionBar called.");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment (String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    //
    // Inner classes begin here
    //

    /**
     * This fragment shows general system settings, such as sound and default Game selection
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "onCreate called in GeneralPreferenceFragment.");
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            setUpMusicToggle();
            setupGameSelector();
            setupPlayerSelector();
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_game_selector_key)));
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item) {
            Log.d(TAG, "onOptionsItemSelected called in GeneralPreferenceFragment.");
            switch (item.getItemId()) {
                case android.R.id.home:
                    isContinueMusic = true;
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        /**
         * This method setups the music toggle so clicking will turn music off and on.
         */
        protected void setUpMusicToggle () {
            findPreference(getString(R.string.pref_music_is_on_key))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick (Preference preference) {
                            if (MUSIC_MANAGER.isReady()) {
                                if (MUSIC_MANAGER.isPlaying()) {
                                    MUSIC_MANAGER.musicPause();
                                } else {
                                    MUSIC_MANAGER.musicStart();
                                }
                            }
                            return false;
                        }
                    });
        }

        /**
         * This method setups the game selector toggle so clicking will open a ListDialog of Games
         */
        protected void setupGameSelector () {
            findPreference(getString(R.string.pref_game_selector_key))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick (Preference preference) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment prev = getFragmentManager().findFragmentByTag("pref_game_selector_key");
                            if (prev != null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);

                            // Create and show the dialog.
                            ListDialogFragment listFragment = new ListDialogFragment();
                            listFragment.setCursor(db.gameDao.getCursorForAdapter(GameDao.ALL_ENTRIES));
                            Bundle args = new Bundle();
                            args.putInt(LIST_CALLER_ID, SET_DEFAULT_GAME);
                            listFragment.setArguments(args);
                            listFragment.show(ft, "pref_game_selector_key");
                            return false;
                        }
                    });
        }

        /**
         * This method setups the game selector toggle so clicking will open a ListDialog of Games
         */
        protected void setupPlayerSelector () {
            findPreference(getString(R.string.pref_player_selector_key))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick (Preference preference) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment prev = getFragmentManager().findFragmentByTag("pref_player_selector_key");
                            if (prev != null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);

                            // Create and show the dialog.
                            ListDialogFragment listFragment = new ListDialogFragment();
                            listFragment.setCursor(db.playerDao.getCursorForAdapter(PlayerDao.ALL_ENTRIES));
                            Bundle args = new Bundle();
                            args.putInt(LIST_CALLER_ID, SET_DEFAULT_PLAYER);
                            listFragment.setArguments(args);
                            listFragment.show(ft, "pref_player_selector_key");
                            return false;
                        }
                    });
        }

    }


    /**
     * This fragment shows Data sync settings, such as sign in registration and timing
     */
    public static class SyncPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "Calling SyncPreferenceFragment");
            addPreferencesFromResource(R.xml.pref_sync);
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows Player settings, such as create, delete, edit
     */
    public static class PlayerPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "Calling PlayerPreferenceFragment");
            addPreferencesFromResource(R.xml.pref_player);
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

