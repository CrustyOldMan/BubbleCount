package ca.chrisbarrett.bubblecount.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Utility class that initializes the Bubble & Soap Typeface statically. This is faster than
 * initializing the Typeface on every call.
 *
 * @author Chris Barrett
 * @see android.graphics.Typeface;
 * @since June 30, 2016
 */
public final class FontCache {

    public static final String FONT_PATH = "fonts/bubble & soap.ttf";
    private static Typeface typeface;

    private FontCache () {
    }

    /**
     * If the "Bubble & Soap" TrueTypeFont is not already cached, the Typeface will be loaded from
     * {@value FontCache#FONT_PATH}.
     *
     * @param context used for calling {@link Context#getAssets()}
     * @return the "Bubble & Soap" TrueTypeFont
     */
    public static Typeface getFont (Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
        }
        return typeface;
    }
}
