package ca.chrisbarrett.bubblecount.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

import ca.chrisbarrett.bubblecount.R;
import ca.chrisbarrett.bubblecount.view.utilities.BubbleFont;

/**
 * Generates a custom Button using the
 * Created by chrisbarrett on 2016-06-29.
 */
public class BubbleButton extends Button {

    public BubbleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        setTypeface(BubbleFont.getFont(context));
    }
}
