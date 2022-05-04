package ui;

import model.Event;
import model.EventLog;
import model.Task;
import model.ToDoList;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

// Command line ToDoList app
public class ToDoListApp {


    private ToDoList toDoList = new ToDoList();
    private Scanner input;
    private String listMode;
    private final String[] listModes = new String[] { "urgencyImportance", "tag",
            "urgent", "important", "today", "thisWeek", "overdue", "finished", "next" };
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private static final String JSON_STORE = "./data/toDoList.json";

    // EFFECTS: runs the To-Do-List application
    public ToDoListApp() {
        runToDoListApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    @SuppressWarnings("methodlength")
    void runToDoListApp() {

        // Initialize
        toDoList = new ToDoList();

        // Uncomment the following to add items for a demo
        // toDoList.addItemsForDemo();

        input = new Scanner(System.in);
        input.useDelimiter("\n");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        boolean keepGoing = true;
        String command;
        listMode = listModes[0];

//        System.out.println("Welcome! This is your To-Do list. Start by adding a new entry.");
//        newEntry();

        // The main loop of the application
        // This keeps the app going
        while (keepGoing) {
            displayToDoList();
            command = promptOptions("\nMenu:",
                    new String[][] {
                            {"ad", "add new task"},
                            {"co", "mark as complete"},
                            {"vi", "change view"},
                            {"lo", "load To-Do List from file"},
                            {"sa", "save To-Do List"},
                            {"cl", "clear list"},
                            {"de", "add tasks for demo"},
                            {"q", "quit"}});

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        System.out.println("Log:");
        for (Event event: EventLog.getInstance()) {
            System.out.println(event.getDescription());
            System.out.println("---------------------");
        }
        System.out.println("\nGoodbye!");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    @SuppressWarnings("methodlength")
    private void processCommand(String command) {
//        {"ad", "add new task"},
//        {"co", "completed task"},
//        {"wn", "what's the next task?"},
//        {"ui", "order by urgency/importance"},
//        {"ta", "order by tags"},
//        {"ur", "urgent tasks"},
//        {"im", "important tasks"},
//        {"to", "today's tasks"},
//        {"ov", "overdue tasks"},
//        {"fi", "show finished tasks"},
//        {"cl", "clear list"}}
//

        String view;
        switch (command) {
            case "ad":
                newEntry();
                break;
            case "co":
                markAsComplete();
                break;
            case "vi":

                view = promptOptions("Which view do you want?", new String[][]{
                        {"ui", "order by urgency/importance"},
                        {"ta", "order by tags"},
                        {"ur", "urgent tasks"},
                        {"im", "important tasks"},
                        {"to", "today's tasks"},
                        {"we", "this week's tasks"},
                        {"ov", "overdue tasks"},
                        {"fi", "show finished tasks"},
                        {"wn", "what's the next task?"},
                        {"q", "quit"}});

                switch (view) {
                    case "ui":
                        listMode = listModes[0];
                        break;
                    case "ta":
                        listMode = listModes[1];
                        break;
                    case "ur":
                        listMode = listModes[2];
                        break;
                    case "im":
                        listMode = listModes[3];
                        break;
                    case "to":
                        listMode = listModes[4];
                        break;
                    case "we":
                        listMode = listModes[5];
                        break;
                    case "ov":
                        listMode = listModes[6];
                        break;
                    case "fi":
                        listMode = listModes[7];
                        break;
                    case "wn":
                        listMode = listModes[8];
                        break;
                }

                break;
            case "lo":
                loadToDoList();
                break;
            case "sa":
                saveToDoList();
                break;
            case "cl":
                toDoList.clearList();
                listMode = listModes[0];
                System.out.println("Cleared the list\nAdd a new task:");
                newEntry();
                break;
            case "de":
                addItemsNotDue();
                break;
            case "q":
                return;
            default:
                System.out.println("Selection not valid...");
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: Prompts user for a new entry
    private void newEntry() {
        LocalDateTime dueDateTime = LocalDateTime.now().plusDays(1);
        ArrayList<String> tags = new ArrayList<String>();

        String task = promptText("What is your task?");

        boolean isDue = promptBoolean("Does your task have a due date?");

        if (isDue) {
            dueDateTime = promptDueDate();
        }

        boolean isUrgent = promptBoolean("Is your task urgent?");
        boolean isImportant = promptBoolean("Is your task important?");
        boolean hasTags = promptBoolean("Would you like to add tags to your task?");
        if (hasTags) {
            tags = promptTags();
        } else {
            tags.add("Other");
        }
        if (isDue) {
            toDoList.addTaskDue(task, dueDateTime, tags, isUrgent, isImportant);
        } else if (!isDue) {
            toDoList.addTask(task, tags, isUrgent, isImportant);
        }
    }

    // MODIFIES: this
    // EFFECTS: Prompts user for a number that represents the task they have completed
    // Removes the task they selected from toDoList
    // If no other task has the tags, also removes the tags
    // If list is empty, prints "list is empty. There is nothing to complete"
    private void markAsComplete() {
        if (!toDoList.isEmpty()) {
            toDoList.printNumberedList();
            int completedTaskIndex = promptInt("\nWhich task did you finish (enter number)? ",
                    1, toDoList.length()) - 1;
            toDoList.taskCompleted(completedTaskIndex);
            System.out.println("Well done!\nMarked \""
                    + toDoList.get(completedTaskIndex).getTask() + "\" as complete!");
        } else {
            System.out.println("List is empty. There is nothing to complete");
        }
    }

    // EFFECTS: Prompts user for due date and time
    // returns LocalDateTime
    @SuppressWarnings("methodlength")
    private LocalDateTime promptDueDate() {
        LocalDateTime dueDateTime = LocalDateTime.now().plusDays(1);
        String dueDateSelectionMethod = promptOptions("How would you like to select the due date?",
                new String[][] { {"m", "manually"}, {"d", "in n days"}, {"h", "in n hours"} });
        if (dueDateSelectionMethod.equals("m")) {
            int year = promptInt("Enter the year of your due date: ", LocalDateTime.now().getYear(), false);
            int month = promptInt("Enter the month of your due date (in number): ", 1, 12);
            int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
            int day = promptInt("Please enter the day of your due date: ", 1, daysInMonth);
            String dueDateMode = promptOptions("Is your due date due midnight, or would you like to specify?",
                    new String[][] { {"m", "due midnight"}, {"s", "specify the time"} });
            if (dueDateMode.equals("m")) {
                dueDateTime = LocalDateTime.of(year, month, day, 23, 59);
            } else if (dueDateMode.equals("s")) {
                int hour = promptInt("Enter the hour of your due date (24 hours, not 12): ", 1, 23);
                int minute = promptInt("Enter the minute of your due date: ", 1, 59);
                dueDateTime = LocalDateTime.of(year, month, day, hour, minute);
            }
        } else if (dueDateSelectionMethod.equals("d")) {
            int inDays = promptInt("In how many days is your task due (e.g. if tomorrow, enter 1): ", 1, false);
            dueDateTime = LocalDateTime.of(LocalDate.now().plusDays(inDays), LocalTime.MAX);
        } else if (dueDateSelectionMethod.equals("h")) {
            int inHours = promptInt("In how many hours is your task due: ", 1, false);
            dueDateTime = LocalDateTime.now().plusHours(inHours);
        }
        return dueDateTime;
    }

    // EFFECTS: Prompts user for tags until user enters 0
    // Returns an ArrayList<String> of tags
    private ArrayList<String> promptTags() {
        ArrayList<String> promptedTags = new ArrayList<String>();
        String tagEntered;
        while (true) {
            System.out.print("Enter a tag (0 to stop adding): ");
            tagEntered = input.nextLine().toLowerCase();
            if (tagEntered.equals("0")) {
                break;
            } else {
                promptedTags.add(tagEntered);
            }
        }
        if (promptedTags.isEmpty()) {
            promptedTags.add("Other");
        }
        return promptedTags;
    }

    // EFFECTS: Prompts user a yes/no question
    // returns true if user inputs 'y'
    // returns false if user input 'n'
    private boolean promptBoolean(String question) {
        String choice;
        while (true) {
            System.out.println(question);
            System.out.println("Select from:");
            System.out.println("\ty -> yes");
            System.out.println("\tn -> no");
            choice = input.nextLine().toLowerCase();
            if (choice.equals("y")) {
                return true;
            } else if (choice.equals("n")) {
                return false;
            } else {
                System.out.println("input not valid...");
            }

        }
    }

    // EFFECTS: Prompts the user for an integer that is:
    // higher than lowBound and lower than highBound
    // keeps prompting until the user enters a right answer and then returns the answer
    private int promptInt(String question, int lowBound, int highBound) {
        int answer;
        while (true) {
            System.out.print(question);
            answer = input.nextInt();
            if (answer >= lowBound && answer <= highBound) {
                return answer;
            } else if (answer < lowBound) {
                System.out.println("Enter a higher number");
            } else if (answer > highBound) {
                System.out.println("Enter a lower number");
            }

        }
    }

    // EFFECTS: Prompts the user for an integer that is:
    // lower than bound if hasHighBound is true
    // higher than bound if hasHighBound is false
    // keeps prompting until the user enters a right answer and then returns the answer
    private int promptInt(String question, int bound, boolean hasHighBound) {
        int answer;
        while (true) {
            System.out.print(question);
            answer = input.nextInt();
            if (hasHighBound) {
                if (answer <= bound) {
                    return answer;
                } else {
                    System.out.println("Enter a smaller number");
                }
            } else if (!hasHighBound) {
                if (answer >= bound) {
                    return answer;
                } else {
                    System.out.println("Enter a bigger number");
                }
            }

        }
    }

    // EFFECTS: Prompts user for an integer and returns the integer
    private int promptInt(String question) {
        System.out.print(question);
        return input.nextInt();
    }

    // EFFECTS: Prompts user until the user chooses a correct answer
    // returns one of the options as a String
    private String promptOptions(String question, String[][] options) {
        String choice;
        while (true) {
            System.out.println(question);
            System.out.println("Select from:");
            for (String[] row: options) {
                System.out.println("\t" + row[0] + " -> " + row[1]);
            }
            choice = input.nextLine().toLowerCase();
            for (String[] row: options) {
                if (choice.equals(row[0]) || choice.equals(row[1])) {
                    return row[0];
                }
            }
            System.out.println("Selection not valid...");

        }
    }

    // EFFECTS: Prompts user a text answer to question
    // Question and user input will be on separate lines
    private String promptText(String question) {
        String answer;
        while (true) {
            System.out.println(question);
            answer = input.nextLine();
            if (!answer.equals("")) {
                return answer;
            }
        }
    }

    // EFFECTS: Prints toDoList based on listMode
    @SuppressWarnings("methodlength")
    private void displayToDoList() {

        System.out.println();
        if (listMode.equals(listModes[0])) {
            System.out.println("****************************");
            System.out.println("*        TO-DO LIST        *");
            System.out.println("****************************");
            toDoList.printTasksByUrgencyAndImportance();
        } else if (listMode.equals(listModes[1])) {
            System.out.println("****************************");
            System.out.println("*        TO-DO LIST        *");
            System.out.println("****************************");
            toDoList.printTasksByTag();
        } else if (listMode.equals(listModes[2])) {
            System.out.println("****************************");
            System.out.println("*          URGENT          *");
            System.out.println("****************************");
            toDoList.printList(toDoList.getUrgent(), true);
        } else if (listMode.equals(listModes[3])) {
            System.out.println("****************************");
            System.out.println("*         IMPORTANT        *");
            System.out.println("****************************");
            toDoList.printList(toDoList.getImportant(), true);
        } else if (listMode.equals(listModes[4])) {
            System.out.println("****************************");
            System.out.println("*           TODAY          *");
            System.out.println("****************************");
            toDoList.printList(toDoList.getToday(), true);
        } else if (listMode.equals(listModes[5])) {
            System.out.println("****************************");
            System.out.println("*         THIS WEEK        *");
            System.out.println("****************************");
            toDoList.printList(toDoList.getThisWeek(), true);
        } else if (listMode.equals(listModes[6])) {
            System.out.println("****************************");
            System.out.println("*          OVERDUE         *");
            System.out.println("****************************");
            toDoList.printList(toDoList.getOverdue(), true);
        } else if (listMode.equals(listModes[7])) {
            System.out.println("****************************");
            System.out.println("*          FINISHED        *");
            System.out.println("****************************");
            toDoList.printList(toDoList.getFinishedTasks(), true);
        } else if (listMode.equals(listModes[8])) {
            System.out.println("****************************");
            System.out.println("*        WHAT'S NEXT?      *");
            System.out.println("****************************");
            toDoList.printList(new ArrayList<Task>(Arrays.asList(toDoList.whatsNext())), true);

        }
    }

    // MODIFIES: this
    // EFFECTS: Adds 13 items
    // For demo
    void addItemsNotDue() {
        // Urgent Important
        toDoList.addTask("Finish CPSC 121 homework", new ArrayList<>(Arrays.asList("university")), true, true);
        toDoList.addTask("Pay university tuition", new ArrayList<>(Arrays.asList("university", "money")), true, true);
        toDoList.addTask("Read CPSC 121 textbook", new ArrayList<>(Arrays.asList("university")), true, true);

        // Not urgent Important
        toDoList.addTask("Go to the gym", new ArrayList<>(Arrays.asList("health")), false, true);
        toDoList.addTask("Get dentist appointment", new ArrayList<>(Arrays.asList("health")), false, true);
        toDoList.addTask("Work", new ArrayList<>(Arrays.asList("work", "money")), false, true);
        toDoList.addTask("Study for midterms", new ArrayList<>(Arrays.asList("university")), false, true);
        toDoList.addTask("Hangout with friends", new ArrayList<>(Arrays.asList("hobbies")), false, true);

        // Urgent Not Important
        toDoList.addTask("Return calls", new ArrayList<>(Arrays.asList("university", "work")), true, false);

        // Not Urgent Not Important
        toDoList.addTask("Get Milk", new ArrayList<>(Arrays.asList("home", "grocery")), false, false);
        toDoList.addTask("Practice guitar", new ArrayList<>(Arrays.asList("hobbies")), false, false);
        toDoList.addTask("Check email", new ArrayList<>(Arrays.asList("university", "work")), false, false);
        toDoList.addTask("Buy eggs", new ArrayList<>(Arrays.asList("home", "grocery")), false, false);
    }

    // EFFECTS: saves the toDoList to file
    private void saveToDoList() {
        try {
            jsonWriter.open();
            jsonWriter.write(toDoList);
            jsonWriter.close();
            System.out.println("Saved To-Do List to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads toDoList from file
    private void loadToDoList() {
        try {
            toDoList = jsonReader.read();
            System.out.println("Loaded To-Do List from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}
