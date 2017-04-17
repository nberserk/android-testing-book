package example.org.todo;

import android.content.Context;
import android.support.annotation.NonNull;


import example.org.todo.model.source.TasksDataSource;
import example.org.todo.model.source.TasksRepository;
import example.org.todo.model.source.local.TasksLocalDataSource;
import example.org.todo.model.source.remote.FakeTasksRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link TasksDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context));
    }
}