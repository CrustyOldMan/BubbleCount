package ca.chrisbarrett.bubblecount.preference;


import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import ca.chrisbarrett.bubblecount.R;
import ca.chrisbarrett.bubblecount.dao.model.Player;

/**
 * Custom DialogPreference that holds a PlayerFeed. This can be used with create or editing of
 * Players. A PlayerFeed object will be returned on save. Players updated or created in this view
 * will not be stored as a preference - instead they will be returned to the calling
 * DialogFragment.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 12, 2016
 */
public class PlayerDialogPreference extends DialogPreference {

    private static final String TAG = "PlayerDialogPreference";
    private Player player;
    private TextView tvTitle;
    private EditText etName;
    private YearPicker yearPicker;

    public PlayerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_player);
        setPersistent(false);
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.d(TAG, attrs.getAttributeName(i) + ":" + attrs.getAttributeValue(i));
        }
    }


    //
    // Inner classes begin here
    //

    /**
     * This class provides a custom year picker for the PlayerFeed
     */
    public class YearPicker extends NumberPicker {

        public YearPicker(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


    }
}