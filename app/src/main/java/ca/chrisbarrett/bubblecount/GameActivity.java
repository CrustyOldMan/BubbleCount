package ca.chrisbarrett.bubblecount;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ca.chrisbarrett.bubblecount.view.GameView;

/**
 * A helper Activity that starts and stops the {@link ca.chrisbarrett.bubblecount.view.GameView;}
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.onResume();
    }
}
