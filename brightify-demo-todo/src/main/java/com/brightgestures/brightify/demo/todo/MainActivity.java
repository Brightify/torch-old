package com.brightgestures.brightify.demo.todo;

import android.app.Activity;
import android.widget.ListView;
import com.brightgestures.brightify.demo.todo.model.Task;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EActivity(R.layout.main_activity)
public class MainActivity extends Activity {

    @ViewById(android.R.id.list)
    ListView tasksList;

    @Bean
    TaskListAdapter adapter;

    @AfterViews
    void bindAdapter() {
        tasksList.setAdapter(adapter);
    }

    @ItemClick
    void tasksListItemClicked(Task task) {

    }

}
