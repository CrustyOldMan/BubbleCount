package ca.chrisbarrett.bubblecount.utilities;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Utility class that initializes the Paint objects statically. This is faster than initializing
 * the Paints on every call. Paints operate as the brush when drawing on a Canvas object
 *
 * @author Chris Barrett
 * @see android.graphics.Paint
 * @since Jul 01, 2016
 */
public class PaintCache {

    public static final int DEFAULT_COLOR = Color.WHITE;
    public static final int DEFAULT_LINE_THICKNESS = 2;
    public static final int DEFAULT_TEXT_SIZE = 120;

    private static Paint textPaint;
    private static Paint drawPaint;

    /**
     * Generates the preset configuration for the text painter. The Paint object is assigned the
     * {@link PaintCache#DEFAULT_COLOR} and {@link PaintCache#DEFAULT_TEXT_SIZE}. Text is also
     * horizontally centered to paint.
     *
     * @return the Paint object used for text
     */
    public static Paint getTextPainter() {
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(DEFAULT_COLOR);
            textPaint.setTextSize(DEFAULT_TEXT_SIZE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
        }
        return textPaint;
    }

    /**
     * Generates the preset configuration for the drawable painter. The Paint object is assigned the
     * {@link PaintCache#DEFAULT_COLOR} and {@link PaintCache#DEFAULT_LINE_THICKNESS}.
     *
     * @return the Paint object used for Drawables
     */
    public static Paint getDrawablePainter() {
        if (drawPaint == null) {
            drawPaint = new Paint();
            drawPaint.setColor(DEFAULT_COLOR);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeWidth(DEFAULT_LINE_THICKNESS);
        }
        return drawPaint;
    }
}
