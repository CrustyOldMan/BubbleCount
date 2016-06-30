package ca.chrisbarrett.bubblecount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import ca.chrisbarrett.bubblecount.view.BubbleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BubbleButton buttonPlay;
    private BubbleButton buttonSettings;
    private ToggleButton toggleMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonPlay = (BubbleButton) findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(this);
        buttonSettings = (BubbleButton) findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(this);
        toggleMusic = (ToggleButton) findViewById(R.id.toggle_music);
        toggleMusic.setOnClickListener(this);
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
                Toast.makeText(this,"button_settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.toggle_music:
                Toast.makeText(this,"toggle_music", Toast.LENGTH_SHORT).show();
                break;
            default:
                // Nothing to see here
        }
    }
}
