package example.org.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by darren on 4/23/17.
 */

public class DueDateDialog extends DialogFragment {
    private static String ARG = "due";

    static DueDateDialog newInstance(long due){
        DueDateDialog frag = new DueDateDialog();
        Bundle args = new Bundle();
        args.putLong(ARG, due);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        long due = getArguments().getLong(ARG);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(due);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlg = new  DatePickerDialog(getActivity(), (TaskDetailActivity)getActivity(), year, month, day);
        dlg.setButton(DatePickerDialog.BUTTON_POSITIVE, "Save", dlg);
        dlg.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Remove", (TaskDetailActivity)getActivity());
        return dlg;
    }
}
