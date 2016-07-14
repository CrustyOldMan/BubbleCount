package ca.chrisbarrett.bubblecount.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * This class provides a sound manager for the background music player. The class can be shared
 * across the Activities and Fragments to allow for seamless transition. Underneath, the class
 * calls on the built-in media player service. The music will be loaded Asynchronously. Callers can
 * implement the OnBackgroundMusicListener.
 * <p/>
 * Giving credit where credit is due - Used for guidance: <a href="http://www.rbgrn
 * .net/content/307-light-racer-20-days-61-64-completion">http://www.rbgrn
 * .net/content/307-light-racer-20-days-61-64-completion</a>. Excellent post by Robert Green on
 * using static music managers. Expanded to use a separate thread for creation.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public class BackgroundMusicManager {

    public static final String TAG = "BackgroundMusicManager";
    private static final String RES_PREFIX = "android.resource://ca.chrisbarrett.bubblecount/";
    private static BackgroundMusicManager ourInstance = new BackgroundMusicManager();

    private boolean isReady;
    private MediaPlayer mediaPlayer;
    private Thread musicThread;
    private OnBackgroundMusicListener listener;

    /**
     * Hidden constructor. Generates a new thread for the music to run on
     */
    private BackgroundMusicManager () {
    }

    /**
     * Returns the Singleton instance of the BackgroundMusicManager
     *
     * @return the BackgroundMusicManager
     */
    public static BackgroundMusicManager getInstance () {
        return ourInstance;
    }

    //
    // LifeCycles Events Begin Here
    //

    /**
     * This method initializes the BackgroundMusicManager. If the mediaPlayer is already
     * initialized, this will be skipped.
     *
     * @param context the applicationContext
     * @param resId   the resource identifying of the sound file
     * @throws ExceptionInInitializerError should something go wrong with loading the mediaPlayer
     * @throws ClassCastException          if the calling class does not implement the
     *                                     {@link ca.chrisbarrett.bubblecount.service.BackgroundMusicManager.OnBackgroundMusicListener}
     */
    public void initialize (final Context context, final int resId) throws
            ExceptionInInitializerError, ClassCastException {
        try {
            listener = (OnBackgroundMusicListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameViewListener");
        }

        if (mediaPlayer == null) {
            Log.d(TAG, "Initializing the BackgroundMusicManager");
            musicThread = new Thread(new Runnable() {
                @Override
                public void run () {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    final Uri uri = Uri.parse(RES_PREFIX + resId);
                    try {
                        mediaPlayer.setDataSource(context, uri);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared (MediaPlayer player) {
                                Log.d(TAG, "BackgroundMusicManager has loaded the resource and isReady.");
                                isReady = true;
                                listener.onMusicReady();
                            }
                        });
                        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError (MediaPlayer mp, int what, int extra) {
                                switch (what) {
                                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                                        isReady = false;
                                        Log.e(TAG, "Unknown Media Error in mediaPlayer. Setting isReady " +
                                                "status to false.");
                                        break;
                                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                                        isReady = false;
                                        Log.e(TAG, "Media server died. Attempting to reinitialize");
                                        try {
                                            mp.setDataSource(context, uri);
                                            mp.prepare();
                                        } catch (IOException e) {
                                            // Already tried twice, giving up...
                                            return false;
                                        }
                                }
                                return true;
                            }
                        });
                        mediaPlayer.prepareAsync();
                    } catch (NullPointerException | IllegalArgumentException | IOException e) {
                        Log.e(TAG, e.getMessage());
                        throw new ExceptionInInitializerError(e.getMessage());
                    }
                }
            });
            musicThread.start();
        }
    }

    /**
     * This method starts the music - if the setup is complete
     *
     * @throws IllegalStateException
     */
    public void musicStart () throws IllegalStateException {
        if (mediaPlayer == null) {
            throw new IllegalStateException("mediaPlayer is null. Call initialize(Context " +
                    "context, int resId) first.");
        } else if (isReady && !mediaPlayer.isPlaying()) {
            Log.d(TAG, "BackgroundMusicManager called to musicStart.");
            mediaPlayer.start();
        }
    }

    /**
     * This method pauses the music - if the music is playing
     * * @throws IllegalStateException
     */
    public void musicPause () throws IllegalStateException {
        if (mediaPlayer == null) {
            throw new IllegalStateException("appContext is null. Call initialize(Context " +
                    "applicationContext, int resId) first.");
        } else if (isReady && mediaPlayer.isPlaying()) {
            Log.d(TAG, "BackgroundMusicManager called to musicPause.");
            mediaPlayer.pause();
        }
    }

    /**
     * This method shuts down the musicManager and releases the mediaPlayer resources
     */
    public void musicRelease () {
        Log.d(TAG, "BackgroundMusicManager called to musicRelease mediaPlayer");
        isReady = false;
        try {
            mediaPlayer.stop();
        } catch (IllegalStateException e) {
            Log.e(TAG, "musicRelease failed on mediaPlayer stop: " + e.getMessage());
        } finally {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (musicThread != null) {
            try {
                musicThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "musicRelease failed on musicThread shutdown: " + e.getMessage());
            }
        }
    }

    //
    // Getters & Setters (if applicable)
    //

    /**
     * Returns the ready state of the BackgroundMusicManager
     *
     * @return true if the BackgroundMusicManager is ready to play music
     */
    public boolean isReady () {
        return isReady;
    }

    /**
     * Returns the play status of the BackgroundMusicManager
     *
     * @return true if the BackgroundMusicManager is playing music
     */
    public boolean isPlaying () {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    //
    // Listeners Begin Here
    //

    /**
     * Assigns the listener to this instance. As this is a Singleton, the listener will change
     * accordingly to the current Activity or Fragment that has implemented the listener.
     *
     * @param listener
     */
    public void setOnBackgroundMusicListener (OnBackgroundMusicListener listener) {
        this.listener = listener;
    }

    //
    // Inner classes begin here
    //

    /**
     * The OnBackgroundMusicListener provides callback services for the BackgroundMusicManager
     */
    public interface OnBackgroundMusicListener {

        /**
         * Called when the BackgroundMusicManager is ready to begin playing music
         */
        void onMusicReady ();
    }
}
