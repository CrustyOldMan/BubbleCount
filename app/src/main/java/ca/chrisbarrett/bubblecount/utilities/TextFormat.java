package ca.chrisbarrett.bubblecount.utilities;

import android.graphics.Paint;

/**
 * Utility class that manipulates text before drawing on the screen
 *
 * @author Chris Barrett
 * @since Jul 01, 2016
 */
public class TextFormat {

    /**
     * Helper method to determine the Y origin for vertically aligning text
     * @param top the top Y value of the bounding box
     * @param bottom the bottom Y value of the bounding box
     * @param paint the Paint object to draw the text
     * @return the Y value required to vertically center the text
     */
    public static float verticalCenter(float top, float bottom, Paint paint) {
        return top + ((bottom  - top) / 2) - ((paint.ascent() + paint.descent()) / 2);
    }
}
