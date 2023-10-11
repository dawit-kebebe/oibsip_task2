package com.dawit.oibsip_task2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dawit.oibsip_task2.db.DatabaseHelper;
import com.dawit.oibsip_task2.model.TodoData;
import com.dawit.oibsip_task2.model.UserData;
import com.dawit.oibsip_task2.ui.AccountDetailPopup;
import com.dawit.oibsip_task2.ui.AddTodoPopup;
import com.dawit.oibsip_task2.ui.MyToast;
import com.dawit.oibsip_task2.ui.TodoListRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

public class HomeActivity extends AppCompatActivity {

    private String uid = "";
    private RecyclerView recyclerView;
    private ArrayList<TodoData> todoDataArrayList;
    private TodoListRecyclerAdapter recyclerViewAdapter;
    private TextView statusTextView;
    private FloatingActionButton floatingActionButton, floatingActionButtonAccount;
    private SharedPreferences sharedPreferences;

    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(HomeActivity.this);
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        if (
                sharedPreferences.contains("uid") &&
                        sharedPreferences.contains("logged_in_at") &&
                        sharedPreferences.contains("expires_at")
        ) {
            Map<String, ?> session = sharedPreferences.getAll();
            String expiresAt = (String) session.get("expires_at");
            this.uid = (String) session.get("uid");
            if (Long.parseLong(expiresAt) < (new Date().getTime())) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                MyToast.makeText(HomeActivity.this, "Session expired.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        setContentView(R.layout.home);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButtonAccount = (FloatingActionButton) findViewById(R.id.floatingActionButtonAccount);
        statusTextView = (TextView) findViewById(R.id.list_item_status);
        recyclerView = (RecyclerView) findViewById(R.id.todo_list_recycler);
        todoDataArrayList = new ArrayList<TodoData>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());

        recyclerViewAdapter = new TodoListRecyclerAdapter(HomeActivity.this, todoDataArrayList, statusTextView);
        floatingActionButton.setOnClickListener(new AddTodoListener());
        floatingActionButtonAccount.setOnClickListener(new CheckAccountListener());

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        (new LoadTodoData()).execute();
    }

    private class LoadTodoData extends AsyncTask<Object, Object, Object> {

//        private final DatabaseHelper databaseHelper;
//        private final TodoListRecyclerAdapter todoListRecyclerAdapter;
//        private ArrayList<TodoData> todoDataArrayList;
//
//        public LoadTodoData(DatabaseHelper databaseHelper, TodoListRecyclerAdapter todoListRecyclerAdapter, ArrayList<TodoData> todoDataArrayList){
//            this.databaseHelper = databaseHelper;
//            this.todoListRecyclerAdapter = todoListRecyclerAdapter;
//            this.todoDataArrayList = todoDataArrayList;
//        }

        @Override
        protected Object doInBackground(Object... objects) {
            ArrayList<TodoData> fetchedTodoList = databaseHelper.getAllTodoItems(uid);
            Stack<TodoData> stack = new Stack<>();
            todoDataArrayList.clear();

            for (int i = 0; i < fetchedTodoList.size(); i++) {
                stack.push(fetchedTodoList.get(i));
            }

            for (int i = 0; i < fetchedTodoList.size(); i++) {
                todoDataArrayList.add(stack.pop());
            }

            return todoDataArrayList;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            recyclerViewAdapter.notifyDataSetChanged();

        }
    }

    private class AddTodoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AddTodoPopup.showAlert(HomeActivity.this, new AddTodoPopup.AlertListener() {
                @Override
                public void onClick(View v) {
                    if (
                            this.getWarningTextView() == null ||
                            this.getTodoTitleEditText() == null ||
                            this.getTodoDetailEditText() == null ||
                            this.getIsCompletedCheckBox() == null
                    ) {
                        MyToast.makeText(HomeActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String title = this.getTodoTitleEditText().getText().toString();
                    String description = this.getTodoDetailEditText().getText().toString();
                    boolean isCompleted = this.getIsCompletedCheckBox().isChecked();

                    if (title.length() <= 3) {
                        this.getWarningTextView().setText("Invalid todo item title.");
                        return;
                    }

                    if (description.length() <= 0) {
                        description = "";
                    }

                    TodoData todoData = new TodoData(title, description, isCompleted, uid);

                    if (databaseHelper.addTodoItem(todoData)) {
                        MyToast.makeText(HomeActivity.this, "Todo item added successfully.", Toast.LENGTH_SHORT).show();
                        this.getWarningTextView().setText("");
                        this.getTodoTitleEditText().setText("");
                        this.getTodoDetailEditText().setText("");
                        this.getIsCompletedCheckBox().setChecked(false);
                        (new LoadTodoData()).execute();
                    }

                }
            }, null, null).show();

        }
    }

    private class CheckAccountListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            UserData userData = databaseHelper.getUser(uid);

            AccountDetailPopup.showAlert(HomeActivity.this, userData, new AccountDetailPopup.AlertListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", this.getUserData().getUserName());
                    bundle.putString("f_name", this.getUserData().getfName());
                    bundle.putString("l_name", this.getUserData().getlName());
                    bundle.putString("password", this.getUserData().getPassword());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }, new AccountDetailPopup.AlertListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor sessionEditor = sharedPreferences.edit();
                    sessionEditor.clear();
                    sessionEditor.apply();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();
        }
    }
}
