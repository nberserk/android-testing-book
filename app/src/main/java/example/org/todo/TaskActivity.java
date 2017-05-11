package example.org.todo;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.org.todo.model.Task;
import example.org.todo.model.source.TasksDataSource;
import example.org.todo.model.source.TasksRepository;

import static android.R.attr.id;
import static com.google.common.base.Preconditions.checkNotNull;
import static example.org.todo.R.id.newTask;

public class TaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "TaskActivity";

    private EditText mEditText;

    private TasksRepository mRepo;
    private TasksAdapter mListAdapter;
    private TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task task) {
            Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.getId());
            startActivity(intent);
            Log.d(TAG, task.getTitle() + " clicked");
        }

        @Override
        public void onCompleteTaskClick(Task task) {
            Task newTask = new Task(task.getTitle(), task.getDescription(), task.getId(), !task.isCompleted(), task.getDueDate(), task.isDueSet(), task.isReminderSet());
            Injection.provideTasksRepository(getApplicationContext()).saveTask(newTask);
            mListAdapter.replaceData();
            //Log.d(TAG, task.getTitle() + " completed");
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //
        mListAdapter = new TasksAdapter(new ArrayList<Task>(0), mItemListener);
        ListView cl = (ListView) findViewById(R.id.list);
        cl.setAdapter(mListAdapter);
        //Log.d("view", cl.toString());







        mEditText = (EditText) findViewById(newTask);
        mEditText.setImeActionLabel("actionDone", KeyEvent.KEYCODE_ENTER);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_DONE) {
                    createTask(textView.getText());
                }
                return false;
            }
        });


        mRepo = Injection.provideTasksRepository(getApplicationContext());

        //loadTask();
    }

    private void createTask(CharSequence text) {
        Task t = new Task(text.toString(), "");
        mRepo.saveTask(t);
        mListAdapter.addTask(t);

        mEditText.getText().clear();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTask();
    }

    private void loadTask() {
        mRepo.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                mListAdapter.replaceData(tasks);
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
    }

    private static class TasksAdapter extends BaseAdapter {

        private List<Task> mTasks;
        private TaskItemListener mItemListener;

        public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
            setList(tasks);
            mItemListener = itemListener;
        }

        public void addTask(@NonNull Task task){
            mTasks.add(task);
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> tasks) {
            mTasks = checkNotNull(tasks);
        }

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int i) {
            return mTasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.task_item, viewGroup, false);
            }

            final Task task = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(task.getTitleForList());


            CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);

            // Active/completed task UI
            if(task.isCompleted()){
                completeCB.setChecked(true);
                titleTV.setPaintFlags(titleTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                completeCB.setChecked(false);
                titleTV.setPaintFlags(titleTV.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

//            if (task.isCompleted()) {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
//            } else {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.touch_feedback));
//            }

            completeCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!task.isCompleted()) {
                        mItemListener.onCompleteTaskClick(task);
                    } else {
                        mItemListener.onActivateTaskClick(task);
                    }
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onTaskClick(task);
                }
            });

            return rowView;
        }
    }

    public interface TaskItemListener {

        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }

}
