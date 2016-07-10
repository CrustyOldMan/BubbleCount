package ca.chrisbarrett.bubblecount;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import ca.chrisbarrett.bubblecount.view.GameView;

/**
 * A helper Activity that starts and stops the {@link ca.chrisbarrett.bubblecount.view.GameView;}
 *
 * @author Chris Barrett
 * @see android.support.v7.app.AppCompatActivity
 * @since Jun 26, 2016
 */
public class GameActivity extends AppCompatActivity implements GameView.OnGameViewListener {

    private GameView gameView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    public void onNewLevel (int level) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage("Level: " + level);
        final AlertDialog alert = dialog.create();
        alert.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run () {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss (DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                gameView.onStart();
            }
        });
        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onGameStart () {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage("Ready? Go!");
        final AlertDialog alert = dialog.create();
        alert.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run () {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss (DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                gameView.onStart();
            }
        });

        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onGameEnd (long time) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage("Total time: " + time);
        final AlertDialog alert = dialog.create();
        alert.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run () {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss (DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                gameView.onStart();
            }
        });

        handler.postDelayed(runnable, 10000);
    }

    @Override
    protected void onPause () {
        super.onPause();
        gameView.onPause();
    }

    @Override
    protected void onResume () {
        super.onResume();
        gameView.onResume();
    }
}
