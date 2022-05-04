package persistence;

import model.Task;
import model.ToDoList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

// Represents a reader that reads toDoList from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads toDoList from file and returns it;
    // throws IOException if an error occurs reading data from file
    public ToDoList read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseToDoList(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses toDoList from JSON object and returns it
    private ToDoList parseToDoList(JSONObject jsonObject) {
        ToDoList list = new ToDoList();
        addTasks(list, jsonObject);
        addTags(list, jsonObject);
        addCompletedTasks(list, jsonObject);
        return list;
    }

    // MODIFIES: list
    // EFFECTS: parses Tasks from JSON object and adds them to toDoList
    private void addTasks(ToDoList list, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("tasks");
        for (Object json : jsonArray) {
            JSONObject nextTask = (JSONObject) json;
            addTask(list, nextTask);
        }
    }

    // MODIFIES: list
    // EFFECTS: parses finishedTasks from JSON object and adds them to toDolist
    private void addCompletedTasks(ToDoList list, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("finishedTasks");
        for (Object json : jsonArray) {
            JSONObject nextTask = (JSONObject) json;
            addTask(list, nextTask);
        }
    }

    // MODIFIES: list
    // EFFECTS: parses Tasks from JSON object and adds them to toDoList
    private void addTags(ToDoList list, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("tags");
        for (Object json : jsonArray) {
            String nextTag = (String) json;
            list.addTag(nextTag);
        }
    }

    // MODIFIES: list
    // EFFECTS: parses thingy from JSON object and adds it to toDoList
    @SuppressWarnings("methodlength")
    private void addTask(ToDoList list, JSONObject jsonObject) {
        String task = jsonObject.getString("task");
        ArrayList<String> tags = parseTaskTags(jsonObject);
        Boolean urgent = jsonObject.getBoolean("urgent");
        Boolean important = jsonObject.getBoolean("important");
        Boolean isDue = jsonObject.getBoolean("isDue");
        LocalDateTime dateTimeAdded = LocalDateTime.parse(jsonObject.getString("dateTimeAdded"));
        Boolean completed = jsonObject.getBoolean("completed");

        Task newTask;

        if (isDue) {
            LocalDateTime dueDateTime = LocalDateTime.parse(jsonObject.getString("dueDateTime"));
            if (!completed) {
                newTask = new Task(task, tags, urgent, important, dueDateTime, dateTimeAdded);
                list.addTask(newTask);
            } else {
                newTask = new Task(task, tags, urgent, important, dueDateTime, dateTimeAdded);
                newTask.setCompleted(true);
                list.addFinishiedTask(newTask);
            }
        } else {
            if (!completed) {
                newTask = new Task(task, tags, urgent, important, dateTimeAdded);
                list.addTask(newTask);
            } else {
                newTask = new Task(task, tags, urgent, important, dateTimeAdded);
                newTask.setCompleted(true);
                list.addFinishiedTask(newTask);
            }
        }
    }

    // EFFECTS: Parses tags from JSONObject and returns them as ArrayList<String>
    private ArrayList<String> parseTaskTags(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("tags");
        ArrayList<String> tags = new ArrayList<String>();
        for (Object json : jsonArray) {
            String nextTag = (String) json;
            tags.add(nextTag);
        }
        return tags;
    }

}