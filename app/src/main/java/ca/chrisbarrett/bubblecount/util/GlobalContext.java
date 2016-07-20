package ca.chrisbarrett.bubblecount.util;

import android.content.Context;

/**
 * Utility Singleton class that holds a a global context. Useful for accessing resources in static methods.
 * From: https://developer.android.com/reference/android/app/Application.html
 *
 * @author Chris Barrett
 * @see android.graphics.Paint
 * @since Jul 01, 2016
 */
public class GlobalContext {
    private static GlobalContext ourInstance = new GlobalContext();
    private Context context;

    private GlobalContext () {
    }

    public static GlobalContext getInstance () {
        return ourInstance;
    }

    public Context getContext () {
        if (context == null) {
            throw new IllegalStateException("Context null! Run GlobalContext#initialize(Context context).");
        }
        return context;
    }

    public void initialize (Context context) {
        if (context == null) {
            this.context = context.getApplicationContext();
        }
    }
}
