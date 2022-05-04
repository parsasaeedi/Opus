# Opus
Opus is a command-line and GUI To-Do List application that helps you prioritize tasks by categorizing them based on **urgency and importance**.

You can either run Opus as a GUI, or a terminal based app. Terminal based has more feautures. 

## About
This time management method was proposed in *The 7 Habits of Highly Effective People* by Stephen Covey:

<img src="data/timeManagementMatrix.png" alt="Time Management Matrix" width="50%"/>

Stephen Covey proposes that the best way to prioritize tasks is by the order of the quadrant: Q1, Q2, Q3, and lastly Q4. The To-Do List is ordered in the same way. There is also a **"what's next"** option that displays your next task by this priority You can also add **tags** to your tasks, such as University, Work, Grocery, etc. There is an option to display your tasks categorized by tag You can add **due dates** to tasks. You can then see today's tasks or this week's tasks

This project is of interest to me because I want to manage my time better by using it more efficiently. This tool can help me or anyone with managing time better.


## How to run jar executables
1. Clone the repository
2. In terminal, navigate to: ./executables
3. Choose and navigate into one of the folders depending on what you want
4. Run the following:
```shell script
java -jar Opus.jar
```
Note: For GUI, you can open the corresponding folder in finder/windows-explorer and double-click the executable jar

## How to run the java files
1. Clone the repository
2. In terminal, navigate to: ./out/production/Opus 
3. Run the following for GUI:
```shell script
java ui.ToDoListGUI
```

or the following for Terminal based:
```shell script
java ui.Main
```

To run a GUI demo, uncomment line 70 in ./src/main/ui/ToDoListGUI.java

To run a Terminal demo, uncomment line 47 in ./src/main/ui/ToDoListApp.java

## User Stories
- As a user, I want to be able to add a task to my to-do list with or without a due date or tag/tags
- As a user, I want to be able to view the list of tasks on my to-do list categorized by urgency and importance
- As a user, I want to be able to view the list of tasks on my to-do list categorized by tags
- As a user, I want to be able to view the list of important tasks on my to-do list
- As a user, I want to be able to view the list of urgent tasks on my to-do list
- As a user, I want to be able to view the list of tasks due today
- As a user, I want to be able to view the list of tasks due this week
- As a user, I want to be able to view the list of overdue tasks
- As a user, I want to be able to mark a task as complete on my to-do list
- As a user, I want to be able to view the completed tasks on my to-do list
- As a user, I want to be able to view the app's suggestion on what task to complete next
- As a user, I want to be able to clear the to-do list (remove all tasks)
- As a user, I want to be able to save my to-do list to file 
- As a user, I want to be able to be able to load my to-do list from file 


## UML Design Diagram
<img src="./UML_Design_Diagram.png" alt="UML_Design_Diagram" width="40%"/>
- If I had more time, I would limit the dependency on EventLog to only ToDoList

## Citation
Used help from JsonSerializationDemo to make To-Do List persistent:
https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
