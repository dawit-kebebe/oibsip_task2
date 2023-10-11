package com.dawit.oibsip_task2.model;

public class TodoData {
    private String todoTitle;
    private String todoDetail;
    private String username;
    private boolean isComplete;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public String getTodoDetail() {
        return todoDetail;
    }

    public String getUsername() {
        return username;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public TodoData(String todoTitle, String todoDetail, boolean isComplete, String username) {
        this.todoTitle = todoTitle;
        this.todoDetail = todoDetail;
        this.isComplete = isComplete;
        this.username = username;
    }
}
