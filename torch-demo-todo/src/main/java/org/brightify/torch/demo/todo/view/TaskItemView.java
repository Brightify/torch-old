package org.brightify.torch.demo.todo.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.brightify.torch.demo.todo.R;
import org.brightify.torch.demo.todo.model.Task;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

import static org.brightify.torch.TorchService.torch;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EViewGroup(R.layout.task_item)
public class TaskItemView extends LinearLayout {

    @ViewById(R.id.title)
    TextView title;

    @ViewById(R.id.completed)
    CheckBox completed;

    Task task;

    public TaskItemView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setCompleted(isChecked);
                refresh();
                torch().save().entity(task).now();
            }
        });
    }

    public void bind(Task task) {
        this.task = task;
        refresh();
    }

    private void refresh() {
        title.setText(task.getTitle());
        completed.setChecked(task.isCompleted());
        title.setTextColor(task.isCompleted() ? Color.GREEN : Color.WHITE);
    }
}
