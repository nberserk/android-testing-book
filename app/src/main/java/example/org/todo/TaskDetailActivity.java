package example.org.todo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import example.org.todo.model.Task;
import example.org.todo.model.source.TasksDataSource;
import example.org.todo.model.source.TasksRepository;

import static example.org.todo.R.menu.task;

public class TaskDetailActivity extends AppCompatActivity {
    private static final String TAG = "TaskDetailAct";

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private TasksRepository mRepo;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        EditText editText = new EditText(getApplicationContext());
//        getSupportActionBar().setCustomView(editText);


        mRepo = Injection.provideTasksRepository(getApplicationContext());
        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        if (taskId!=null){
            Log.d(TAG, "tid: " + taskId);
            mRepo.getTask(taskId, new TasksDataSource.GetTaskCallback() {
                @Override
                public void onTaskLoaded(Task task) {
                    mTask=task;
                    getSupportActionBar().setTitle(task.getTitle());
                }
                @Override
                public void onDataNotAvailable() {
                }
            });
        }



    }

}
