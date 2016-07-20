package ca.chrisbarrett.bubblecount.view.game.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

import ca.chrisbarrett.bubblecount.util.Values;

/**
 * Used to hold an image for a sprite
 *
 * @author Chris Barrett
 * @see ca.chrisbarrett.bubblecount.view.game.model.Sprite
 * @since Jul 19, 2016
 */
public class SpriteImage {

    private static final String TAG = "SpriteImage";

    private Bitmap image;
    private int totalFrames;
    private int frameHeight;
    private int frameWidth;
    private Rect whatToDraw;
    private int currentFrame;
    private boolean isSquish;

    public SpriteImage (Bitmap image, int totalFrames) {
        isSquish = Values.RANDOM.nextBoolean();
        this.totalFrames = totalFrames;
        this.image = image;
        this.frameHeight = image.getHeight();
        this.frameWidth = image.getWidth() / totalFrames;
        int frame = Values.RANDOM.nextInt(totalFrames);
        this.whatToDraw = new Rect(frame * frameWidth, 0, frame * frameWidth + frameWidth, frameHeight);
    }

    public Bitmap getImage () {
        return image;
    }

    public Rect getWhatToDraw () {
        return whatToDraw;
    }

    public void animate () {
        if (++currentFrame % 12 == 0) {
            isSquish = !isSquish;
        }
        if (isSquish) {
            whatToDraw.set(whatToDraw.left + 1, whatToDraw.top - 1, whatToDraw.right - 1, whatToDraw.bottom + 1);
        } else {
            whatToDraw.set(whatToDraw.left - 1, whatToDraw.top + 1, whatToDraw.right + 1, whatToDraw.bottom - 1);
        }
    }
}
