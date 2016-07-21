package ca.chrisbarrett.bubblecount.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ca.chrisbarrett.bubblecount.R;

/**
 * This Dialog generates a List view in a Dialog using a CursorAdapter<br>
 * <b>Note: </b> The Cursor will be closed when the Dialog is destroyed. It cannot be retrieved.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 16, 2016
 */
public class ListDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    public static final int NO_CALLER_ID = -1;
    public static final String LIST_CALLER_ID = "LIST_CALLER_ID";

    private static final String TAG = "ListDialogFragment";
    private Cursor cursor;
    private ListView listView;
    private OnListDialogItemClickListener callbackListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called.");
        View view = inflater.inflate(R.layout.fragment_listdialog, null, false);
        listView = (ListView) view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called.");
        super.onActivityCreated(savedInstanceState);
        CursorAdapter adapter = new ListCursorAdapter(getActivity(), cursor, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackListener = (OnListDialogItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnListDialogItemClickListener");
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.d(TAG, "onItemClick called.");
        if (callbackListener != null) {
            int callerId = getArguments().getInt(LIST_CALLER_ID, NO_CALLER_ID);
            Log.d(TAG, String.format("Firing OnListDialogItemClick with callerId: %d, position: %d, id: %d",
                    callerId, position, id));
            callbackListener.OnListDialogItemClick(callerId, position, id);
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();     // TODO - If things crash, it's probably this
    }

    /**
     * This inner class defines the Listener a user of the {@link ListDialogFragment} should implement.
     */
    public interface OnListDialogItemClickListener {

        /**
         * On user ItemClick, a callback containging hte
         *
         * @param position
         * @param callerId
         * @param id
         */
        void OnListDialogItemClick(int callerId, int position, long id);
    }

    /**
     * This inner class defines the Adapter for the ListDialog
     */
    public class ListCursorAdapter extends CursorAdapter {

        private static final String TAG = "ListCursorAdapter";

        private LayoutInflater cursorInflater;

        public ListCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return cursorInflater.inflate(R.layout.listdialog_row_layout, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView title = (TextView) view.findViewById(R.id.list_row_text);
            title.setText(cursor.getString(1));
        }
    }
}

