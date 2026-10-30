package ru.example.blockingqueuetasks;

public class Task {
    private final String url;
    private final int id;

    public Task(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() { return id; }
    public String getUrl() { return url; }

    @Override
    public String toString() {
        return "Task#" + id + " (" + url + ")";
    }
}