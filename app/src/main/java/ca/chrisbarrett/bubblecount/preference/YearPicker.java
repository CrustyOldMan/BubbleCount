package ca.chrisbarrett.bubblecount.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * A custom NumberPicker used to pick years
 *
 * @author Chris Barrett
 * @see
 * @since Jun 26, 2016
 */
public class YearPicker extends NumberPicker{

    public YearPicker (Context context) {
        super(context);
    }

    public YearPicker (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YearPicker (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
