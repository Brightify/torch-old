package com.brightgestures.brightify.demo.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.brightgestures.brightify.demo.todo.model.Task;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.SystemService;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EFragment
public class EditDialog extends DialogFragment {

    @SystemService
    LayoutInflater inflater;

    Task taskToEdit;
    OnEditSaveListener editSaveListener;
    OnEditDeleteListener editDeleteListener;
    OnEditCancelListener editCancelListener;
    EditText title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.edit_fragment, null);
        title = (EditText) view.findViewById(R.id.title);
        alertDialogBuilder.setView(view);

        if (taskToEdit == null) {
            alertDialogBuilder.setTitle("Add task");
        } else {
            alertDialogBuilder.setTitle("Edit task");
            title.setText(taskToEdit.getTitle());
        }

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editCancelListener.onEditCancel();
            }
        });
        alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Task outputTask = taskToEdit;
                if(outputTask == null) {
                    outputTask = new Task();
                } else if(title.getText().toString().equals("")) {
                    editDeleteListener.onEditDelete(outputTask);
                    return;
                }

                if(title.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Title cannot be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                outputTask.setTitle(title.getText().toString());
                editSaveListener.onEditSave(outputTask);
            }
        });

        return alertDialogBuilder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnEditSaveListener) {
            editSaveListener = (OnEditSaveListener) activity;
        } else {
            throw new IllegalStateException("Parent activity has to implement OnEditSaveListener");
        }

        if(activity instanceof OnEditDeleteListener) {
            editDeleteListener = (OnEditDeleteListener) activity;
        } else {
            throw new IllegalStateException("Parent activity has to implement OnEditDeleteListener");
        }

        if(activity instanceof OnEditCancelListener) {
            editCancelListener = (OnEditCancelListener) activity;
        } else {
            throw new IllegalStateException("Parent activity has to implement OnEditCancelListener");
        }

    }

    public static EditDialog_ edit(Task task) {
        EditDialog_ dialog = new EditDialog_();
        dialog.taskToEdit = task;
        return dialog;
    }

    interface OnEditSaveListener {
        void onEditSave(Task task);
    }

    interface OnEditDeleteListener {
        void onEditDelete(Task task);
    }

    interface OnEditCancelListener {
        void onEditCancel();
    }


}
