package com.dawit.oibsip_task2.ui;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dawit.oibsip_task2.HomeActivity;
import com.dawit.oibsip_task2.R;
import com.dawit.oibsip_task2.db.DatabaseHelper;
import com.dawit.oibsip_task2.model.TodoData;

import java.util.ArrayList;
import java.util.Stack;

public class TodoListRecyclerAdapter extends RecyclerView.Adapter<TodoListRecyclerAdapter.ViewHolder> {

    public static TodoListRecyclerAdapter currentObj;
    private ArrayList<TodoData> todoDataArrayList;
    private Context context;
    private TextView statusTextView;

    public ArrayList<TodoData> getTodoDataArrayList(){
        return this.todoDataArrayList;
    }

    public TodoListRecyclerAdapter(Context context, ArrayList<TodoData> todoDataArrayList, TextView statusTextView){
        this.context = context;
        this.todoDataArrayList = todoDataArrayList;
        this.statusTextView = statusTextView;
        if (this.todoDataArrayList.size() == 0) {
            this.statusTextView.setText("- No more todo item -");
        }

        currentObj = this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.todo_list_container, parent, false);
        if (this.todoDataArrayList.size() == 0) {
            this.statusTextView.setText("- No more todo item -");
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoData todoData = this.todoDataArrayList.get(position);
        holder.titleTextView.setText(todoData.getTodoTitle());
        holder.detailTextView.setText(todoData.getTodoDetail());
//        holder.todoItemIdTextView.setText(String.valueOf(todoData.getId()));
//        holder.todoItemOwnerTextView.setText(todoData.getUsername());
        holder.itemView.setOnClickListener(new ItemSelectedListener(todoData));

        Log.e("-- TODO DATA --", String.valueOf(todoData.getId()));

        if (todoData.isComplete()) {
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.detailTextView.setPaintFlags(holder.detailTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.detailTextView.setPaintFlags(holder.detailTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return this.todoDataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView detailTextView;
        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.titleTextView = itemView.findViewById(R.id.todo_title);
            this.detailTextView = itemView.findViewById(R.id.todo_detail);
            this.itemView = itemView;
        }
    }

    public static class ItemSelectedListener implements View.OnClickListener {

        private TodoData todoData;

        public ItemSelectedListener(TodoData todoData) {
            this.todoData = todoData;
        }

        @Override
        public void onClick(View v) {
            AddTodoPopup.showAlert(v.getContext(), new AddTodoPopup.AlertListener() {
                @Override
                public void onClick(View v) {
                    if (
                            this.getWarningTextView() == null ||
                                    this.getTodoTitleEditText() == null ||
                                    this.getTodoDetailEditText() == null ||
                                    this.getIsCompletedCheckBox() == null ||
                                    this.getTodoData() == null
                    ) {
                        MyToast.makeText(v.getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
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

                    DatabaseHelper databaseHelper = new DatabaseHelper(v.getContext());

                    TodoData todoData = new TodoData(title, description, isCompleted, this.getTodoData().getUsername());
                    todoData.setId(this.getTodoData().getId());

                    if (databaseHelper.updateTodo(todoData)) {
                        MyToast.makeText(v.getContext(), "Todo item updated successfully.", Toast.LENGTH_SHORT).show();
                        if (AddTodoPopup.getDialog() != null) {
                            AddTodoPopup.getDialog().dismiss();
                        }
                        (new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... objects) {
                                ArrayList<TodoData> fetchedTodoList = databaseHelper.getAllTodoItems(todoData.getUsername());
                                Stack<TodoData> stack = new Stack<>();
                                TodoListRecyclerAdapter.currentObj.getTodoDataArrayList().clear();

                                for (int i = 0; i < fetchedTodoList.size(); i++) {
                                    stack.push(fetchedTodoList.get(i));
                                }

                                for (int i = 0; i < fetchedTodoList.size(); i++) {
                                    TodoListRecyclerAdapter.currentObj.getTodoDataArrayList().add(stack.pop());
                                }

                                return TodoListRecyclerAdapter.currentObj.getTodoDataArrayList();
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                TodoListRecyclerAdapter.currentObj.notifyDataSetChanged();
                            }
                        }).execute();
                    }
                }
            }, new AddTodoPopup.AlertListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(v.getContext());
                    (new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... objects) {
                            if (databaseHelper.deleteTodoItem(todoData)) {
                                ArrayList<TodoData> fetchedTodoList = databaseHelper.getAllTodoItems(todoData.getUsername());
                                Stack<TodoData> stack = new Stack<>();
                                TodoListRecyclerAdapter.currentObj.getTodoDataArrayList().clear();

                                for (int i = 0; i < fetchedTodoList.size(); i++) {
                                    stack.push(fetchedTodoList.get(i));
                                }

                                for (int i = 0; i < fetchedTodoList.size(); i++) {
                                    TodoListRecyclerAdapter.currentObj.getTodoDataArrayList().add(stack.pop());
                                }

                                return TodoListRecyclerAdapter.currentObj.getTodoDataArrayList();
                            }else {
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if (o != null)
                                TodoListRecyclerAdapter.currentObj.notifyDataSetChanged();
                        }
                    }).execute();
                    if (AddTodoPopup.getDialog() != null) {
                        AddTodoPopup.getDialog().dismiss();
                    }
                }
            }, todoData).show();
        }
    }
}
