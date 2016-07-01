package ca.chrisbarrett.bubblecount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import ca.chrisbarrett.bubblecount.view.BubbleButton;

/**
 * A main "splash" activity of the app. Provides two buttons (play the game, access the
 * settings menu). Also starts the music player and loads Players from the database in the
 * background.
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private BubbleButton buttonPlay;
    private BubbleButton buttonSettings;
    private ToggleButton toggleMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            buttonPlay = (BubbleButton) findViewById(R.id.button_play);
            buttonPlay.setOnClickListener(this);
            buttonSettings = (BubbleButton) findViewById(R.id.button_settings);
            buttonSettings.setOnClickListener(this);
            toggleMusic = (ToggleButton) findViewById(R.id.toggle_music);
            toggleMusic.setOnClickListener(this);
        } catch (NullPointerException e){
            Log.e(TAG, "Assigning OnClickListener failure: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play:
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.button_settings:
                Toast.makeText(this, "button_settings", Toast.LENGTH_SHORT).show();
                //TODO - Implement SettingsActivity
                break;
            case R.id.toggle_music:
                Toast.makeText(this, "toggle_music", Toast.LENGTH_SHORT).show();
                //TODO - Implement Music Player and toggle to turn off and on
                break;
            default:
                // Nothing to see here
        }
    }
}
