package ca.chrisbarrett.bubblecount.preference;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import ca.chrisbarrett.bubblecount.R;
import ca.chrisbarrett.bubblecount.dao.model.Player;

/**
 * This Preference class displays an editing tool for Player details
 *
 * @author Chris Barrett
 * @see
 * @since Jul 16, 2016
 */
public class PlayerDialogPreference extends DialogPreference {

    private static final String TAG = "PlayerDialogPreference";
    private Player player;
    private TextView tvTitle;
    private EditText etName;
    private YearPicker yearPicker;
    private NumberPicker numberPicker;
    private OnPlayerDialogPreferenceListener listener;

    public PlayerDialogPreference (Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_player);
        setPersistent(false);
    }

    @Override
    protected void onBindDialogView (View view) {
        super.onBindDialogView(view);
        etName = (EditText) view.findViewById(R.id.player_edittext_name);
        yearPicker = (YearPicker) view.findViewById(R.id.player_year_picker);
        yearPicker.setMaxValue(2015);
        yearPicker.setMinValue(2000);
        yearPicker.setValue(2010);
        yearPicker.setWrapSelectorWheel(false);
    }

    @Override
    protected void onDialogClosed (boolean positiveResult) {
        if (positiveResult) {
            Log.d(TAG, String.format("Player Updated - Name: %s, Year: %d", etName.getText().toString(), yearPicker.getValue()));
            Bundle response = new Bundle();
            response.putString("name", etName.getText().toString());
            response.putInt("year", yearPicker.getValue());
            listener.onPlayerUpdate(response);
        }
        super.onDialogClosed(positiveResult);
    }

    public interface OnPlayerDialogPreferenceListener {

        /**
         * Returns a Bundle containing the Player update results
         *
         * @param result
         */
        public void onPlayerUpdate (Bundle result);
    }
}