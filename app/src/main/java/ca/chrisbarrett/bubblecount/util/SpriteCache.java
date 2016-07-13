package ca.chrisbarrett.bubblecount.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ca.chrisbarrett.bubblecount.R;

/**
 * Utility class that loads the sprites for display.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 02, 2016
 */
public final class SpriteCache {

    private static Bitmap bubbleSprite;

    private SpriteCache () {
    }

    /**
     * If the Bubble sprite is not already cached, the Sprite will be loaded from
     *
     * @param context used for calling
     * @return the Bubble sprite
     */
    public static Bitmap getSprite (Context context) {
        if (bubbleSprite == null) {
            bubbleSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_sprite);
        }
        return bubbleSprite;
    }
}
