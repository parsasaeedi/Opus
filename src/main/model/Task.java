package model;

import org.json.JSONObject;
import persistence.Writable;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task implements Writable {

    // Fields
    private String task;
    private ArrayList<String> tags;
    private Boolean urgent;
    private Boolean important;
    private Boolean isDue;
    private LocalDateTime dueDateTime;
    private LocalDateTime dateTimeAdded;
    private Boolean completed;
    private int id;
    private static int count;

    // Constructors

    // Task due constructor
    public Task(String task, ArrayList<String> tags, Boolean urgent, Boolean important,
                LocalDateTime dueDateTime, LocalDateTime dateTimeAdded) {
        this.task = task;
        this.tags = tags;
        this.urgent = urgent;
        this.important = important;
        this.isDue = true;
        this.dueDateTime = dueDateTime;
        this.dateTimeAdded = dateTimeAdded;
        this.completed = false;
        this.id = count;
        count++;
    }

    // Task not due constructor
    public Task(String task, ArrayList<String> tags, Boolean urgent, Boolean important, LocalDateTime dateTimeAdded) {
        this.task = task;
        this.tags = tags;
        this.urgent = urgent;
        this.important = important;
        this.isDue = false;
        this.dueDateTime = null;
        this.dateTimeAdded = dateTimeAdded;
        this.completed = false;
        this.id = count;
        count++;
    }




    // Methods

    // Getters
    public String getTask() {
        return task;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public Boolean getImportant() {
        return important;
    }

    public Boolean isDue() {
        return isDue;
    }

    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    public int getId() {
        return id;
    }

    //    public Boolean getCompleted() {
//        return completed;
//    }

//    public LocalDateTime getDateTimeAdded() {
//        return dateTimeAdded;
//    }

    // Setters
//    public void setTask(String task) {
//        this.task = task;
//    }

//    public void setTags(ArrayList<String> tags) {
//        this.tags = tags;
//    }

//    public void setUrgent(Boolean urgent) {
//        this.urgent = urgent;
//    }

//    public void setImportant(Boolean important) {
//        this.important = important;
//    }

//    public void setDueDateTime(LocalDateTime dueDateTime) {
//        this.dueDateTime = dueDateTime;
//    }

//    public void setisDue(Boolean due) {
//        isDue = due;
//    }


    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

//    public void setDateTimeAdded(LocalDateTime dateTimeAdded) {
//        this.dateTimeAdded = dateTimeAdded;
//    }


    @Override
    public String toString() {
        return task;
    }

    // EFFECTS: Returns this task as a JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("task", task);
        json.put("tags", tags);
        json.put("urgent", urgent);
        json.put("important", important);
        json.put("isDue", isDue);
        if (isDue) {
            json.put("dueDateTime", dueDateTime.toString());
        } else {
            json.put("dueDateTime", (String) null);
        }
        json.put("dateTimeAdded", dateTimeAdded);
        json.put("completed", completed);
        return json;
    }
}
