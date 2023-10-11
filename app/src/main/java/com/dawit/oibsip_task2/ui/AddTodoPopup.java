package com.dawit.oibsip_task2.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dawit.oibsip_task2.R;
import com.dawit.oibsip_task2.model.TodoData;

public class AddTodoPopup {
    private static TodoData todoData;
    private static Dialog dialog;
    private static TextView warningTextView, menuTitleTextView;
    private static EditText todoTitleEditText;
    private static EditText todoDetailEditText;
    private static CheckBox isCompletedCheckBox;
    private static Button addItemBtn;
    private static ImageButton deleteItemBtn;

    public static Dialog showAlert(Context context, AddTodoPopup.AlertListener confirmListener, AddTodoPopup.AlertListener deleteItemListener, TodoData todoData){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.add_todo_layout, null);

        menuTitleTextView = (TextView) view.findViewById(R.id.menu_title);
        warningTextView = (TextView) view.findViewById(R.id.warning_txt);
        todoTitleEditText = (EditText) view.findViewById(R.id.todo_title);
        todoDetailEditText = (EditText) view.findViewById(R.id.todo_detail);
        isCompletedCheckBox = (CheckBox) view.findViewById(R.id.item_completed);
        deleteItemBtn = (ImageButton) view.findViewById(R.id.delete_btn);

        addItemBtn = (Button) view.findViewById(R.id.add_item_btn);
        confirmListener.initComponents(warningTextView, todoTitleEditText, todoDetailEditText, isCompletedCheckBox, todoData);
        addItemBtn.setOnClickListener(confirmListener);

        if (todoData != null) {
            menuTitleTextView.setText(R.string.UPDATE_TODO_ITEM);
            warningTextView.setText("");
            todoTitleEditText.setText(todoData.getTodoTitle());
            todoDetailEditText.setText(todoData.getTodoDetail());
            isCompletedCheckBox.setChecked(todoData.isComplete());
            deleteItemBtn.setVisibility(View.VISIBLE);
            deleteItemBtn.setOnClickListener(deleteItemListener);
            AddTodoPopup.todoData = todoData;
            addItemBtn.setText(R.string.UPDATE_TODO_ITEM);
        }

        Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        AddTodoPopup.dialog = dialog;
        return dialog;
    }

    public static Dialog getDialog(){
        return AddTodoPopup.dialog;
    }

    public static abstract class AlertListener implements View.OnClickListener {

        private TextView warningTextView;
        private EditText todoTitleEditText;
        private EditText todoDetailEditText;
        private TodoData todoData;

        public TodoData getTodoData() {
            return todoData;
        }

        public TextView getWarningTextView() {
            return warningTextView;
        }

        public EditText getTodoTitleEditText() {
            return todoTitleEditText;
        }

        public EditText getTodoDetailEditText() {
            return todoDetailEditText;
        }

        public CheckBox getIsCompletedCheckBox() {
            return isCompletedCheckBox;
        }

        private CheckBox isCompletedCheckBox;

        public void initComponents(
                TextView warningTextView, EditText todoTitleEditText,
                EditText todoDetailEditText, CheckBox isCompletedCheckBox, TodoData todoData
        ) {
            this.warningTextView = warningTextView;
            this.todoTitleEditText = todoTitleEditText;
            this.todoDetailEditText = todoDetailEditText;
            this.isCompletedCheckBox = isCompletedCheckBox;
            this.todoData = todoData;
        }
    }
}
