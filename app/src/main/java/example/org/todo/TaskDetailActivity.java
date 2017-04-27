package example.org.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.base.Strings;

import java.util.Calendar;

import example.org.todo.model.Task;
import example.org.todo.model.source.TasksDataSource;
import example.org.todo.model.source.TasksRepository;

import static example.org.todo.R.menu.task;

public class TaskDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TaskDetailAct";
    public static final String EXTRA_TASK_ID = "TASK_ID";

    private TasksRepository mRepo;

    @Nullable
    private String mTaskId;
    private EditText mTitle;
    private EditText mDesc;
    private TextView mDueDateText;
    private Calendar mCalendar = Calendar.getInstance();
    private TextView mTimeText;
    private boolean mIsReminderSet;
    private boolean mIsDueSet;
    private boolean mIsDateShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_task_detail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });
//        EditText editText = new EditText(getApplicationContext());
//        getSupportActionBar().setCustomView(editText);

        // ui
        mTitle = (EditText)findViewById(R.id.title);
        mDesc = (EditText) findViewById(R.id.description);
        mDueDateText = (TextView) findViewById(R.id.duedate);
        mTimeText = (TextView) findViewById(R.id.time);

        //
        mRepo = Injection.provideTasksRepository(getApplicationContext());
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        if (!Strings.isNullOrEmpty(mTaskId)){
            Log.d(TAG, "tid: " + mTaskId);
            mRepo.getTask(mTaskId, new TasksDataSource.GetTaskCallback() {
                @Override
                public void onTaskLoaded(Task task) {
                    getSupportActionBar().setTitle(task.getTitle());
                    mTitle.setText(task.getTitle());
                    mDesc.setText(task.getDescription());

                    if (task.isDueSet()){
                        setDueDate(task.getDueDate());
                        if(task.isReminderSet()){
                            setReminder(task.getDueDate());
                        }
                    }


                }
                @Override
                public void onDataNotAvailable() {
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.taskdetail_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mRepo.deleteTask(mTaskId);
                finish();
                return true;
        }
        return false;
    }

    boolean isValidTaskId(){
        if (Strings.isNullOrEmpty(mTaskId)){
            return false;
        }
        return true;
    }

    private void saveTask(){
        if (isValidTaskId()){
            mRepo.saveTask(new Task(mTitle.getText().toString(), mDesc.getText().toString(), mTaskId, mCalendar.getTimeInMillis(), mIsDueSet, mIsReminderSet));
        }
    }

    public void onClickDueDate(View v){
        mIsDateShow=true;
        DueDateDialog newFragment =  DueDateDialog.newInstance(mCalendar.getTimeInMillis());
        newFragment.show(getFragmentManager(), "DueDateDialog");
    }

    private void setDueDate(long milli){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milli);
        setDueDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    private void setDueDate(int year, int month, int day){
        mCalendar.set(year, month, day);
        mDueDateText.setText(String.format("Due: %1$tb %1$te, %1$tY", mCalendar.getTimeInMillis()));
        mIsDueSet=true;
    }

    private void removeDueDate(){
        mDueDateText.setText("Set Due Date");
        mIsDueSet=false;
    }

    public void onClickTime(View v){
        mIsDateShow=false;
        TimeSetDialog dlg = TimeSetDialog.newInstance(mCalendar.getTimeInMillis());
        dlg.show(getFragmentManager(), "TimeSetDialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setDueDate(year, month, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        setReminder(hour, minute);
    }

    private void setReminder(long milli){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milli);

        setReminder(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
    }

    private void setReminder(int hour, int minute){
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        mTimeText.setText(String.format("Reminder: %1$tb %1$te %1$tY at %1$tR", mCalendar.getTimeInMillis()));
        mIsReminderSet=true;
    }

    private void removeReminder() {
        mIsReminderSet=false;
        mTimeText.setText("Reminder: ");
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(mIsDateShow){
            removeDueDate();
        }else {
            removeReminder();
        }
    }



}
