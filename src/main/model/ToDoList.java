package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
//import java.util.List;

public class ToDoList implements Writable {

    // Constructor
    public ToDoList() {
        toDoList = new ArrayList<Task>();
        tags = new ArrayList<String>();
        finishedTasks = new ArrayList<Task>();
    }

    // Fields
    private ArrayList<Task> toDoList;
    private ArrayList<String> tags;
    private ArrayList<Task> finishedTasks;


    // Methods

    // MODIFIES: This
    // EFFECTS: Creates a new Task that isn't due, urgent, or important with the tag 'Other'
    // and adds it to the toDoList
    public void addTask(String task) {
        addTask(task, new ArrayList<>(Arrays.asList("Other")), false, false);
    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that isn't due, urgent, or important
    // and adds it to the toDoList
    public void addTask(String task, String tab) {
        addTask(task, new ArrayList<>(Arrays.asList(tab)), false, false);
    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task with the tag 'Other' and adds it to the toDoList
//    public void addTask(String task, boolean urgent, boolean important) {
//        addTask(task, new ArrayList<>(Arrays.asList("Other")), urgent, important);
//    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that isn't due, urgent, or important and adds it to the toDoList
    // If the tags of the task aren't in the tags list, then adds them
//    public void addTask(String task, ArrayList<String> tags) {
//        addTask(task, tags, false, false);
//    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that isn't due and adds it to the toDoList
    // If the tags of the task aren't in the tags list, then adds them
    public void addTask(String task, ArrayList<String> tags, boolean urgent, boolean important) {
        toDoList.add(new Task(task, tags, urgent, important, LocalDateTime.now()));
        for (String tag: tags) {
            if (!this.tags.contains(tag)) {
                this.tags.add(tag);
            }
        }

        String logText = "Added task not due to toDoList\nTask: " + task + "\n";
        logText += getLogString(tags, urgent, important);
        EventLog.getInstance().logEvent(new Event(logText));
    }

    // MODIFIES: This
    // Takes a Task as an argument and adds it to the toDoList
    // Doesn't add tags
    // Used mainly for loading toDoList from file
    public void addTask(Task task) {
        toDoList.add(task);
    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that is due and adds it to the toDoList
    // If the tags of the task aren't in the tags list, then adds them
    public void addTaskDue(String task, LocalDateTime dueDateTime, ArrayList<String> tags,
                           boolean urgent, boolean important) {
        toDoList.add(new Task(task, tags, urgent, important, dueDateTime, LocalDateTime.now()));
        for (String tag: tags) {
            if (!this.tags.contains(tag)) {
                this.tags.add(tag);
            }
        }

        String logText = "Added task due to toDoList\nTask: " + task + "\n";
        logText += getLogString(tags, urgent, important);
        logText += "\nDue: " + formatDate(dueDateTime);
        EventLog.getInstance().logEvent(new Event(logText));
    }

    // MODIFIES: This
    // EFFECTS: Takes String and adds it to tags
    public void addTag(String tag) {
        tags.add(tag);
        EventLog.getInstance().logEvent(new Event("Added tag '" + tag + "' to tags"));
    }

    // MODIFIES: This
    // EFFECTS: Takes a Task and adds it to finishedTasks
    public void addFinishiedTask(Task task) {
        finishedTasks.add(task);

        String logText = "Added task to finished tasks\nTask: " + task + "\n";
        logText += getLogString(task.getTags(), task.getUrgent(), task.getImportant());
        EventLog.getInstance().logEvent(new Event(logText));
    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that is due, isn't urgent and isn't important with the 'Other' tag
    // and adds it to the toDoList
//    public void addTaskDue(String task, LocalDateTime dueDateTime) {
//        addTaskDue(task, dueDateTime, new ArrayList<>(Arrays.asList("Other")), false, false);
//    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that is due with the 'Other' tag and adds it to the toDoList
//    public void addTaskDue(String task, LocalDateTime dueDateTime, boolean urgent, boolean important) {
//        addTaskDue(task, dueDateTime, new ArrayList<>(Arrays.asList("Other")), urgent, important);
//    }

    // MODIFIES: This
    // EFFECTS: Creates a new Task that is due with the 'Other' tag and adds it to the toDoList
//    public void addTaskDue(String task, LocalDateTime dueDateTime, ArrayList<String> tags) {
//        addTaskDue(task, dueDateTime, tags, false, false);
//    }

    // EFFECTS: Returns toDoList
    // Returns an empty ArrayList<Task> if toDoList is empty
    public ArrayList<Task> getAllTasks() {
        return toDoList;
    }

    public Task get(int taskIndex) {
        return toDoList.get(taskIndex);
    }

    // EFFECTS: Returns an ArrayList of Tasks as a subset of items based on urgency and importance
    // Returns an empty ArrayList<Task> if toDoList is empty
    public ArrayList<Task> getTasks(boolean urgent, boolean important) {
        ArrayList<Task> temp = new ArrayList<Task>();
        for (Task task : toDoList) {
            if (task.getUrgent() == urgent && task.getImportant() == important) {
                temp.add(task);
            }
        }
        return temp;
    }

    // EFFECTS: Returns the first item based on this priority:
    // urgent and important - not urgent and important - important and not urgent - not important and not urgent
    // If the list is empty, then returns a new task called "Relax!"
    public Task whatsNext() {
        if (!toDoList.isEmpty()) {
            ArrayList<Task> temp = getTasks(true, true);
            if (!temp.isEmpty()) {
                return temp.get(0);
            }
            temp = getTasks(false, true);
            if (!temp.isEmpty()) {
                return temp.get(0);
            }
            temp = getTasks(true, false);
            if (!temp.isEmpty()) {
                return temp.get(0);
            }
            temp = getTasks(false, false);
            if (!temp.isEmpty()) {
                return temp.get(0);
            }
        }
        return new Task("Relax!", new ArrayList<>(Arrays.asList("Other")), false, false, LocalDateTime.now());
    }

    // EFFECTS: Returns tasks that are due today
    // Returns an empty ArrayList<Task> if toDoList is empty
    public ArrayList<Task> getToday() {
        ArrayList<Task> temp = new ArrayList<Task>();
        if (!toDoList.isEmpty()) {
            for (Task task : toDoList) {
                if (task.isDue()) {
                    if (LocalDate.now().compareTo(task.getDueDateTime().toLocalDate()) == 0) {
                        temp.add(task);
                    }
                }
            }
        }
        return temp;
    }

    // EFFECTS: Returns tasks that are due this week
    // Returns an empty ArrayList<Task> if toDoList is empty
    public ArrayList<Task> getThisWeek() {
        ArrayList<Task> temp = new ArrayList<Task>();
        if (!toDoList.isEmpty()) {
            for (Task task : toDoList) {
                if (task.isDue()) {
                    if (task.getDueDateTime().toLocalDate().isBefore(LocalDate.now().plusDays(7))
                            && task.getDueDateTime().toLocalDate().isAfter(LocalDate.now().minusDays(1)))  {
                        temp.add(task);
                    }
                }
            }
        }
        return temp;
    }

    // EFFECTS: Returns tasks that are overdue
    // Returns an empty ArrayList<Task> if toDoList is empty
    public ArrayList<Task> getOverdue() {
        ArrayList<Task> temp = new ArrayList<Task>();
        if (!toDoList.isEmpty()) {
            for (Task task : toDoList) {
                if (task.isDue()) {
                    if (task.getDueDateTime().isBefore(LocalDateTime.now()))  {
                        temp.add(task);
                    }
                }
            }
        }
        return temp;
    }

//    // MODIFIES: This
//    // EFFECTS: Removes task from toDoList after adding it to finishedTasks
//    // If the tag/tags of the task aren't used in any other tasks, removes them from the tags
//    // Returns true if removal was successful, false if unsuccessful, including the case that the toDoList is empty.
//    @SuppressWarnings("methodlength")
//    public boolean taskCompleted(String task) {
//        if (toDoList.isEmpty()) {
//            return false;
//        }
//        for (int i = 0; i < toDoList.size(); i++) {
//            if (task.equalsIgnoreCase(toDoList.get(i).getTask())) {
//                ArrayList<String> tagsOfTheTask = toDoList.get(i).getTags();
//                boolean tagIsUsedElseWhere;
//                for (String tag: tagsOfTheTask) {
//                    tagIsUsedElseWhere = false;
//                    for (Task listItem: toDoList) {
//                        if (listItem.getTags().contains(tag)
//                                && !listItem.getTask().equalsIgnoreCase(toDoList.get(i).getTask())) {
//                            tagIsUsedElseWhere = true;
//                            break;
//                        }
//                    }
//                    if (!tagIsUsedElseWhere) {
//                        tags.remove(tag);
//                    }
//                }
//                toDoList.get(i).setCompleted(true);
//                finishedTasks.add(toDoList.get(i));
//                toDoList.remove(i);
//                return true;
//            }
//        }
//        return false;
//    }

    // MODIFIES: This
    // EFFECTS: Removes task from toDoList after adding it to finishedTasks
    // If the tag/tags of the task aren't used in any other tasks, removes them from the tags
    // Returns true if removal was successful, false if unsuccessful, including the case that the toDoList is empty.
    @SuppressWarnings("methodlength")
    public boolean taskCompleted(int taskIndex) {
        if (toDoList.isEmpty()) {
            return false;
        } else {
            Task task = toDoList.get(taskIndex);
            boolean tagIsUsedElseWhere;
            for (String tag: task.getTags()) {
                tagIsUsedElseWhere = false;
                for (Task listItem: toDoList) {
                    if (listItem.getTags().contains(tag)
                            && !listItem.getTask().equalsIgnoreCase(task.getTask())) {
                        tagIsUsedElseWhere = true;
                        break;
                    }
                }
                if (!tagIsUsedElseWhere) {
                    tags.remove(tag);
                }
            }

            EventLog.getInstance().logEvent(new Event("Task completed: " + task.getTask()));
            task.setCompleted(true);
            finishedTasks.add(task);
            toDoList.remove(taskIndex);

            return true;
        }
    }

    // MODIFIES: This
    // EFFECTS: Removes task from toDoList after adding it to finishedTasks
    // If the tag/tags of the task aren't used in any other tasks, removes them from the tags
    // Returns true if removal was successful, false if unsuccessful, including the case that the toDoList is empty.
    @SuppressWarnings("methodlength")
    public boolean taskCompletedById(int id) {
        if (toDoList.isEmpty()) {
            return false;
        }
        for (Task task: toDoList) {
            if (task.getId() == id) {
                ArrayList<String> tagsOfTheTask = task.getTags();
                boolean tagIsUsedElseWhere;
                for (String tag: tagsOfTheTask) {
                    tagIsUsedElseWhere = false;
                    for (Task listItem: toDoList) {
                        if (listItem.getTags().contains(tag)
                                && !listItem.getTask().equalsIgnoreCase(task.getTask())) {
                            tagIsUsedElseWhere = true;
                            break;
                        }
                    }
                    if (!tagIsUsedElseWhere) {
                        tags.remove(tag);
                    }
                }

                EventLog.getInstance().logEvent(new Event("Task completed: " + task.getTask()));

                task.setCompleted(true);
                finishedTasks.add(task);
                toDoList.remove(task);
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: Clears the toDoList and tags arrayList
    public void clearList() {
        toDoList.clear();
        tags.clear();

        EventLog.getInstance().logEvent(new Event("toDoList cleared"));
    }


    // EFFECTS: Returns tags
    public ArrayList<String> getTags() {
        return tags;
    }

    // EFFECTS: Returns an ArrayList of Tasks with the specified tag
    public ArrayList<Task> getByTag(String tag) {
        ArrayList<Task> temp = new ArrayList<Task>();
        for (Task task : toDoList) {
            if (task.getTags().contains(tag)) {
                temp.add(task);
            }
        }
        return temp;
    }

    // EFFECTS: Returns an ArrayList of Tasks that are urgent
    public ArrayList<Task> getUrgent() {
        ArrayList<Task> temp = new ArrayList<Task>();
        for (Task task : toDoList) {
            if (task.getUrgent()) {
                temp.add(task);
            }
        }
        return temp;
    }

    // EFFECTS: Returns an ArrayList of Tasks that are not urgent
    public ArrayList<Task> getNotUrgent() {
        ArrayList<Task> temp = new ArrayList<Task>();
        for (Task task : toDoList) {
            if (!task.getUrgent()) {
                temp.add(task);
            }
        }
        return temp;
    }

    // EFFECTS: Returns an ArrayList of Tasks that are important
    public ArrayList<Task> getImportant() {
        ArrayList<Task> temp = new ArrayList<Task>();
        for (Task task : toDoList) {
            if (task.getImportant()) {
                temp.add(task);
            }
        }
        return temp;
    }

    // EFFECTS: Returns an ArrayList of Tasks that are not important
    public ArrayList<Task> getNotImportant() {
        ArrayList<Task> temp = new ArrayList<Task>();
        for (Task task : toDoList) {
            if (!task.getImportant()) {
                temp.add(task);
            }
        }
        return temp;
    }

    // EFFECTS: Returns tasks that have been completed
    // returns an empty ArrayList<Task> if no tasks have been completed
    public ArrayList<Task> getFinishedTasks() {
        return finishedTasks;
    }

    // EFFECTS: Returns the length of the to do list
    public int length() {
        return toDoList.size();
    }


    // EFFECTS: Returns true if the to-do list is empty, false otherwise
    public boolean isEmpty() {
        return toDoList.isEmpty();
    }

    // EFFECTS: Prints all tasks in the order they were added with a number
    // Used when prompting the user which task they've completed
    public void printNumberedList() {
        if (!toDoList.isEmpty()) {
            int i = 1;
            for (Task task: toDoList) {
                System.out.print(i + " -> " + task.getTask());

                if (task.isDue()) {
                    System.out.println(" (Due " + formatDate(task.getDueDateTime()) + ")");
                } else {
                    System.out.println();
                }

                i++;
            }
        }
    }

    // EFFECTS: Prints the inputted ArrayList<Task>
    // Prints tags next to each task if showTags is true, won't print it otherwise
    // Will always print the due date, if it has one
    // If the inputted list is empty, prints "Empty list"
    @SuppressWarnings("methodlength")
    public void printList(ArrayList<Task> listSubset, boolean showTags) {
        if (!listSubset.isEmpty()) {
            for (Task listItem: listSubset) {
                System.out.print(listItem.getTask());
//                if (listItem.getUrgent() && listItem.getImportant()) {
//                    System.out.println("Urgent\t\tImportant");
//                } else if (!listItem.getUrgent() && listItem.getImportant()) {
//                    System.out.println("Not Urgent\t\tImportant");
//                } else if (listItem.getUrgent() && !listItem.getImportant()) {
//                    System.out.println("Urgent\t\t\tNot Important");
//                } else if (!listItem.getUrgent() && !listItem.getImportant()) {
//                    System.out.println("Not Urgent\t\tNot Important");
//                }

                if (showTags) {
                    if (!listItem.getTags().isEmpty()) {
                        System.out.print(" [");
                        for (String tag : listItem.getTags()) {
                            System.out.print(tag + ", ");
                        }
                        System.out.print("\b\b");
                        System.out.print("]");
                    }
                }
                if (listItem.isDue()) {
                    System.out.println(" (Due " + formatDate(listItem.getDueDateTime()) + ")");
                } else {
                    System.out.println();
                }
            }
        } else {
            System.out.println("Empty list");
        }
    }

    // EFFECTS: Prints toDoList grouped by urgency and importance
    // If the inputted list is empty, prints "Empty list"
    // Prints the tags next to the tasks
    // Prints the due date next to the tasks, if they have one
    @SuppressWarnings("methodlength")
    public void printTasksByUrgencyAndImportance() {
        if (toDoList.isEmpty()) {
            System.out.println("Empty list");
            return;
        }
        ArrayList<Task> importantUrgent = getTasks(true, true);
        ArrayList<Task> importantNotUrgent = getTasks(false, true);
        ArrayList<Task> notImportantUrgent = getTasks(true, false);
        ArrayList<Task> notImportantNotUrgent = getTasks(false, false);

        if (!importantUrgent.isEmpty()) {
            System.out.println("----------------------------");
            System.out.println("Important and Urgent");
            System.out.println("----------------------------");
            printList(importantUrgent, true);
        }
        if (!importantNotUrgent.isEmpty()) {
            System.out.println("\n----------------------------");
            System.out.println("Important and Not Urgent");
            System.out.println("----------------------------");
            printList(importantNotUrgent, true);
        }
        if (!notImportantUrgent.isEmpty()) {
            System.out.println("\n----------------------------");
            System.out.println("Not Important and Urgent");
            System.out.println("----------------------------");
            printList(notImportantUrgent, true);
        }
        if (!notImportantNotUrgent.isEmpty()) {
            System.out.println("\n----------------------------");
            System.out.println("Not Important and Not Urgent");
            System.out.println("----------------------------");
            printList(notImportantNotUrgent, true);
        }
        System.out.println();
    }

    // EFFECTS: Prints tasks grouped by tags
    // If the inputted list is empty, prints "Empty list"
    // Prints the due date next to the tasks, if they have one
    public void printTasksByTag() {
        if (toDoList.isEmpty()) {
            System.out.println("Empty list");
            return;
        }
        for (String tag: tags) {
            System.out.println("----------------------------");
            System.out.println(tag.toUpperCase());
            System.out.println("----------------------------");
            printList(getByTag(tag), false);
            System.out.println();
        }
    }

    public ArrayList<String> tasksToStringList(ArrayList<Task> list) {
        ArrayList<String> stringList = new ArrayList<String>();
        for (Task task: list) {
            stringList.add("" + task.getTask());
        }
        return stringList;
    }

    // EFFECTS: Returns a formatted string of dateTime
    // In the format "E, MMM dd 'at' hh:mm a"
    // e.g.: "2021, Oct, 15 at 05:00 PM
    public String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd 'at' hh:mm a");
        return dateTime.format(myFormatObj);
    }

    // EFFECTS: returns toDoList as a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("tasks", tasksToJson());
        json.put("tags", tagsToJson());
        json.put("finishedTasks", finishedTasksToJson());

        return json;
    }

    // EFFECTS: returns tasks in this toDoList as a JSON array
    private JSONArray tasksToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Task task : toDoList) {
            jsonArray.put(task.toJson());
        }

        return jsonArray;
    }

    // EFFECTS: returns tags in this toDoList as a JSON array
    private JSONArray tagsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (String tag : tags) {
            jsonArray.put(tag);
        }

        return jsonArray;
    }

    // EFFECTS: returns completed tasks in this toDoList as a JSON array
    private JSONArray finishedTasksToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Task task : finishedTasks) {
            jsonArray.put(task.toJson());
        }

        return jsonArray;
    }

    // MODIFIES: this
    // EFFECTS: Adds 13 items
    // For demo
    public void addItemsForDemo() {
        // Urgent Important
        addTask("Finish CPSC 121 homework", new ArrayList<>(Arrays.asList("University")), true, true);
        addTask("Pay university tuition", new ArrayList<>(Arrays.asList("University", "Money")), true, true);
        addTask("Read CPSC 121 textbook", new ArrayList<>(Arrays.asList("University")), true, true);

        // Not urgent Important
        addTask("Go to the gym", new ArrayList<>(Arrays.asList("Health")), false, true);
        addTask("Get dentist appointment", new ArrayList<>(Arrays.asList("Health")), false, true);
        addTask("Work", new ArrayList<>(Arrays.asList("Work", "Money")), false, true);
        addTask("Study for midterms", new ArrayList<>(Arrays.asList("University")), false, true);
        addTask("Hangout with friends", new ArrayList<>(Arrays.asList("Hobbies")), false, true);

        // Urgent Not Important
        addTask("Return calls", new ArrayList<>(Arrays.asList("University", "Work")), true, false);

        // Not Urgent Not Important
        addTask("Get Milk", new ArrayList<>(Arrays.asList("Home", "Grocery")), false, false);
        addTask("Practice guitar", new ArrayList<>(Arrays.asList("Hobbies")), false, false);
        addTask("Check email", new ArrayList<>(Arrays.asList("University", "Work")), false, false);
        addTask("Buy eggs", new ArrayList<>(Arrays.asList("Home", "Grocery")), false, false);
    }

    // Creates and returns a String about the information of tasks
    private String getLogString(ArrayList<String> tags, boolean urgent, boolean important) {
        String logText = "";

        if (urgent) {
            logText += "Urgent, ";
        } else {
            logText += "Not Urgent, ";
        }

        if (important) {
            logText += "Important\n";
        } else {
            logText += "Not Important\n";
        }

        logText += "Tags: ";
        StringJoiner joiner = new StringJoiner(", ");
        tags.forEach(item -> joiner.add(item.toString()));
        logText += joiner.toString();

        return logText;
    }

}
