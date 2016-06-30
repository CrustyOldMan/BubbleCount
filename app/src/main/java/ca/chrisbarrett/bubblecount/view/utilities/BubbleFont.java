package ca.chrisbarrett.bubblecount.view.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Utility class that loads the BubbleSprite & Soap Typeface statically
 */
public class BubbleFont {

    private static Typeface typeface;

    /**
     * Returns the BubbleSprite & Soap Typeface
     *
     * @return
     */
    public static Typeface getFont(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/bubble & soap.ttf");
        }
        return typeface;
    }
}
