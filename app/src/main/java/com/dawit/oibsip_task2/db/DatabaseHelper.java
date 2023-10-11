package com.dawit.oibsip_task2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dawit.oibsip_task2.model.TodoData;
import com.dawit.oibsip_task2.model.UserData;
import com.dawit.oibsip_task2.ui.MyToast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String dbName = "todolist_app";
    private final String userTable = "user";
    private final String todoTable = "todo";

    public DatabaseHelper(@Nullable Context context) {
        super(context, dbName, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    userTable +
                    " (`id` integer not null primary key autoincrement," +
                    " `f_name` varchar(255) NOT NULL," +
                    " `l_name` varchar(255) NOT NULL," +
                    " `username` varchar(255) NOT NULL UNIQUE," +
                    " `password` varchar(255) NOT NULL" +
            ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    todoTable +
                    " (`id` integer not null primary key autoincrement," +
                    " `title` VARCHAR(255)," +
                    " `detail` LARGETEXT," +
                    " `isCompleted` BOOLEAN DEFAULT 0," +
                    " `username` VARCHAR(255) NOT NULL," +
                    " CONSTRAINT username FOREIGN KEY (username) REFERENCES user(username)" +
            ");");
        }catch(Exception ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on setting up db: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE IF EXISTS " + this.userTable + ";");
            db.execSQL("DROP TABLE IF EXISTS " + this.todoTable + ";");
        }catch(Exception ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on updating the db version: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
        onCreate(db);
    }

    public boolean signupUser(UserData userData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (sqLiteDatabase == null){
            Toast.makeText(context, "Couldn't get a writable database", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("f_name", userData.getfName());
        contentValues.put("l_name", userData.getlName());
        contentValues.put("username", userData.getUserName());
        contentValues.put("password", userData.getPassword());

        try{
            long rowId = sqLiteDatabase.insertOrThrow(userTable, null, contentValues);
            return (-1L != rowId);
        }catch (SQLException ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on signing you up: " + ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public UserData getUser(String username) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        if (sqLiteDatabase == null){
            Toast.makeText(context, "Couldn't get a readable database", Toast.LENGTH_LONG).show();
            return null;
        }

        try{

            UserData userData = null;

            Cursor cursor = sqLiteDatabase.query(userTable, new String[]{
                    "f_name", "l_name", "username", "password"
            }, "username = ?", new String[]{
                    username
            }, null, null, null);

            while(cursor.moveToFirst()){
                userData = new UserData(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                return userData;
            }
        }catch (SQLException ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on signing you up: " + ex.toString(), Toast.LENGTH_LONG).show();
        }

        return null;
    }

    public boolean loginUser(UserData userData) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        if (sqLiteDatabase == null){
            Toast.makeText(context, "Couldn't get a readable database", Toast.LENGTH_LONG).show();
            return false;
        }

        try{

            Cursor cursor = sqLiteDatabase.query(userTable, new String[]{
                    "f_name", "l_name", "username", "password"
            }, "username = ? AND password = ?", new String[]{
                    userData.getUserName(), userData.getPassword()
            }, null, null, null);

            return cursor.moveToNext();

        }catch (SQLException ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on signing you up: " + ex.toString(), Toast.LENGTH_LONG).show();
        }

        return false;
    }

    public boolean addTodoItem(TodoData todoData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (sqLiteDatabase == null){
            MyToast.makeText(context, "Couldn't get a writable database", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", todoData.getTodoTitle());
        contentValues.put("detail", todoData.getTodoDetail());
        contentValues.put("isCompleted", todoData.isComplete());
        contentValues.put("username", todoData.getUsername());

        try{
            long rowId = sqLiteDatabase.insertOrThrow(todoTable, null, contentValues);
            return (-1L != rowId);
        }catch (SQLException ex){
            Log.w("DB", ex);
            MyToast.makeText(context, "Error on adding your todo item: " + ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean updateTodo(TodoData todoData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (sqLiteDatabase == null){
            MyToast.makeText(context, "Couldn't get a writable database", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("id", todoData.getId());
        contentValues.put("title", todoData.getTodoTitle());
        contentValues.put("detail", todoData.getTodoDetail());
        contentValues.put("isCompleted", todoData.isComplete());
        contentValues.put("username", todoData.getUsername());

        try{
            long rowId = sqLiteDatabase.replaceOrThrow(todoTable, null, contentValues);
            return (-1L != rowId);
        }catch (SQLException ex){
            Log.w("DB", ex);
            MyToast.makeText(context, "Error on adding your todo item: " + ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean deleteTodoItem(TodoData todoData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (sqLiteDatabase == null){
            MyToast.makeText(context, "Couldn't get a writable database", Toast.LENGTH_LONG).show();
            return false;
        }

        try{
            long rowId = sqLiteDatabase.delete(todoTable, "id = ? AND username = ?", new String[] {String.valueOf(todoData.getId()), todoData.getUsername()}); //.replaceOrThrow(todoTable, null, contentValues);
            return (-1L != rowId);
        }catch (SQLException ex){
            Log.w("DB", ex);
            MyToast.makeText(context, "Error on removing your todo item: " + ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public ArrayList<TodoData> getAllTodoItems(String username) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        if (sqLiteDatabase == null){
            Toast.makeText(context, "Couldn't get a readable database", Toast.LENGTH_LONG).show();
            return null;
        }

        try{
            ArrayList<TodoData> todoDataArrayList = new ArrayList<>();
            TodoData todoData = null;

            Cursor cursor = sqLiteDatabase.query(todoTable, null, "username = ?", new String[]{
                    username
            }, null, null, null);


            while(cursor.moveToNext()){
                todoData = new TodoData(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3) == 0 ? false : true,
                        cursor.getString(4)
                );
                todoData.setId(cursor.getInt(0));
                todoDataArrayList.add(todoData);
            }

            return todoDataArrayList;
        }catch (SQLException ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on signing you up: " + ex.toString(), Toast.LENGTH_LONG).show();
        }

        return null;
    }

    public boolean editUser(UserData userData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (sqLiteDatabase == null){
            Toast.makeText(context, "Couldn't get a writable database", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("f_name", userData.getfName());
        contentValues.put("l_name", userData.getlName());
        contentValues.put("username", userData.getUserName());
        contentValues.put("password", userData.getPassword());

        try{
            long rowId = sqLiteDatabase.replaceOrThrow(userTable, null, contentValues);
            return (-1L != rowId);
        }catch (SQLException ex){
            Log.w("DB", ex);
            Toast.makeText(context, "Error on signing you up: " + ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
