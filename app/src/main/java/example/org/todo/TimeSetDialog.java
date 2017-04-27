package example.org.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by darren on 4/23/17.
 */

public class TimeSetDialog extends DialogFragment {
    private static String ARG = "due";
    public static int BUTTON_REMOVE = 4;

    static TimeSetDialog newInstance(long due){
        TimeSetDialog frag = new TimeSetDialog();
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
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog dlg = new  TimePickerDialog(getActivity(),(TaskDetailActivity)getActivity(), time, minute, true);
        dlg.setButton(DatePickerDialog.BUTTON_POSITIVE, "Save", dlg);
        dlg.setButton(BUTTON_REMOVE, "Remove", (TaskDetailActivity)getActivity());
        return dlg;
    }
}
