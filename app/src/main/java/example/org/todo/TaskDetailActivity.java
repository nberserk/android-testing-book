package example.org.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Strings;

import example.org.todo.model.Task;
import example.org.todo.model.source.TasksDataSource;
import example.org.todo.model.source.TasksRepository;

import static example.org.todo.R.menu.task;

public class TaskDetailActivity extends AppCompatActivity implements ReminderDialog.ReminderDialogListener {
    private static final String TAG = "TaskDetailAct";

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private TasksRepository mRepo;

    @Nullable
    private String mTaskId;
    private EditText mTitle;
    private EditText mDesc;

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
        findViewById(R.id.setReminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderDialog newFragment = new ReminderDialog();
                newFragment.show(getFragmentManager(), "oops");
//                newFragment.startActivityForResult();
                //newFragment.show(getSupportFragmentManager(), "missiles");

            }
        });
        mTitle = (EditText)findViewById(R.id.title);
        mDesc = (EditText) findViewById(R.id.description);

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
            mRepo.saveTask(new Task(mTitle.getText().toString(), mDesc.getText().toString(), mTaskId));
        }
    }

    @Override
    public void onDialogPositiveClick(ReminderDialog dialog) {
        //dialog.mCalendar.
    }

    @Override
    public void onDialogNegativeClick(ReminderDialog dialog) {
        // do nothing
    }
}
