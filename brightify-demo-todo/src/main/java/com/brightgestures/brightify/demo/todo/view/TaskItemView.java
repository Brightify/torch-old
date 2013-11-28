package com.brightgestures.brightify.demo.todo.view;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.brightgestures.brightify.demo.todo.R;
import com.brightgestures.brightify.demo.todo.model.Task;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EViewGroup(R.layout.task_item)
public class TaskItemView extends LinearLayout {

    @ViewById(R.id.title)
    TextView title;

    @ViewById(R.id.completed)
    CheckBox completed;

    public TaskItemView(Context context) {
        super(context);
    }

    public void bind(Task task) {
        title.setText(task.getTitle());
        completed.setChecked(task.isCompleted());
    }
}
