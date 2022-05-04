package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoListTest {

    ToDoList toDoList;
    // todo: Check how the methods would react to wrong inputs
    // todo: Write more tests

    @BeforeEach
    void runBefore() {
        toDoList = new ToDoList();
    }

    @Test
    void testAddTaskNotDue() {
        addItemsNotDue();
        assertEquals(13, toDoList.length());
        // todo test if tags get duplicated
    }

    @Test
    void testAddTaskDue() {
        toDoList.addTaskDue("Buy toasts", LocalDateTime.of(2021, 10, 15, 19, 0), new ArrayList<>(Arrays.asList("Home", "Grocery")), false, true);
        toDoList.addTaskDue("Finish CPSC 210 homework", LocalDateTime.of(2021, 10, 15, 23, 59), new ArrayList<>(Arrays.asList("University")), false, true);
        assertEquals(toDoList.length(), 2);
        // todo test if tags get duplicated
    }

    @Test
    void testGetAllTasks() {
        addItemsNotDue();
        assertEquals(13, toDoList.getAllTasks().size());
    }

    @Test
    void testGet() {
        addItemsNotDue();
        assertEquals("Finish CPSC 121 homework", toDoList.get(0).getTask());
    }

    @Test
    void testGetTasks() {
        addItemsNotDue();
        assertEquals(3, toDoList.getTasks(true, true).size());
        assertEquals(5, toDoList.getTasks(false, true).size());
        assertEquals(1, toDoList.getTasks(true, false).size());
        assertEquals(4, toDoList.getTasks(false, false).size());
    }

    @Test
    void testWhatsNext() {
        assertEquals("Relax!", toDoList.whatsNext().getTask());
        addItemsNotDue();
        assertEquals("Finish CPSC 121 homework", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Pay university tuition", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Read CPSC 121 textbook", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);

        assertEquals("Go to the gym", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Get dentist appointment", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Work", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Study for midterms", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Hangout with friends", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);

        assertEquals("Return calls", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);

        assertEquals("Get Milk", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Practice guitar", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Check email", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);
        assertEquals("Buy eggs", toDoList.whatsNext().getTask());
        toDoList.taskCompleted(0);

        assertEquals("Relax!", toDoList.whatsNext().getTask());

    }

    // The test would fail between 23:50 and 00:00 everyday
    @Test
    void testGetToday() {
        toDoList.addTaskDue("Buy toasts", LocalDateTime.now().plusMinutes(5), new ArrayList<>(Arrays.asList("Home", "Grocery")), false, true);
        toDoList.addTaskDue("Finish CPSC 210 homework", LocalDateTime.now().plusMinutes(10), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Read the CPSC 121 textbook", LocalDateTime.now().plusDays(1), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Do the pre-lecture quizzes", LocalDateTime.now().plusDays(2), new ArrayList<>(Arrays.asList("University")), false, true);

        assertEquals(2, toDoList.getToday().size());
    }

    @Test
    void testGetThisWeek() {
        toDoList.addTaskDue("Buy toasts", LocalDateTime.now().plusMinutes(5), new ArrayList<>(Arrays.asList("Home", "Grocery")), false, true);
        toDoList.addTaskDue("Finish CPSC 210 homework", LocalDateTime.now().plusDays(1), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Work on the CPSC 210 homework", LocalDateTime.now().plusDays(4), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Read the CPSC 121 textbook", LocalDateTime.now().plusDays(6), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Do the pre-lecture quizzes", LocalDateTime.now().plusDays(7), new ArrayList<>(Arrays.asList("University")), false, true);

        assertEquals(4, toDoList.getThisWeek().size());
    }

    @Test
    void testGetOverdue() {
        toDoList.addTaskDue("Do the pre-lecture quizzes", LocalDateTime.now().minusMinutes(5), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Finish CPSC 210 homework", LocalDateTime.now().minusDays(1), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Read the CPSC 121 textbook", LocalDateTime.now().minusDays(6), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Work on the CPSC 210 homework", LocalDateTime.now().plusDays(4), new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTaskDue("Buy toasts", LocalDateTime.now().plusMinutes(5), new ArrayList<>(Arrays.asList("Home", "Grocery")), false, true);

        assertEquals(3, toDoList.getOverdue().size());
    }

//    @Test
//    void testTaskCompleted() {
//
//        assertFalse(toDoList.taskCompleted("Some task"));
//
//        addItemsNotDue();
//
//        toDoList.taskCompleted("Finish CPSC 121 homework");
//        toDoList.taskCompleted("Pay university tuition");
//        toDoList.taskCompleted("Get dentist appointment");
//        toDoList.taskCompleted("Return calls");
//        toDoList.taskCompleted("Buy eggs");
//
//        assertEquals(7, toDoList.getTags().size());
//        toDoList.taskCompleted("Get Milk");
//
//        assertEquals(5, toDoList.getTags().size());
//
//        assertEquals(7, toDoList.length());
//    }

    @Test
    void testTastkCompleted() {
        assertFalse(toDoList.taskCompleted(20));

        addItemsNotDue();
        toDoList.taskCompleted(0);
        toDoList.taskCompleted(0);
        toDoList.taskCompleted(2);
        toDoList.taskCompleted(5);
        toDoList.taskCompleted(8);

        assertEquals(7, toDoList.getTags().size());
        toDoList.taskCompleted(5);

        assertEquals(5, toDoList.getTags().size());
        assertEquals(7, toDoList.length());
    }

    @Test
    void testClearList() {
        addItemsNotDue();
        toDoList.clearList();
        assertEquals(0, toDoList.length());
        assertEquals(0, toDoList.getTags().size());
    }

    @Test
    void testGetTags() {
        addItemsNotDue();
        assertEquals(7, toDoList.getTags().size());
    }

    @Test
    void testGetByTag() {
        addItemsNotDue();
        assertEquals(6, toDoList.getByTag("University").size());
        assertEquals(2, toDoList.getByTag("Money").size());
        assertEquals(2, toDoList.getByTag("Health").size());
        assertEquals(3, toDoList.getByTag("Work").size());
        assertEquals(2, toDoList.getByTag("Hobbies").size());
        assertEquals(2, toDoList.getByTag("Home").size());
        assertEquals(2, toDoList.getByTag("Grocery").size());
    }

    @Test
    void testGetUrgent() {
        addItemsNotDue();
        assertEquals(4, toDoList.getUrgent().size());
    }

    @Test
    void testGetNotUrgent() {
        addItemsNotDue();
        assertEquals(9, toDoList.getNotUrgent().size());
    }

    @Test
    void testGetImportant() {
        addItemsNotDue();
        assertEquals(8, toDoList.getImportant().size());
    }

    @Test
    void testGetNotImportant() {
        addItemsNotDue();
        assertEquals(5, toDoList.getNotImportant().size());
    }

    @Test
    void testGetFinishedTasks() {
        addItemsNotDue();
        assertEquals(0, toDoList.getFinishedTasks().size());
        toDoList.taskCompleted(0);
        toDoList.taskCompleted(0);
        toDoList.taskCompleted(0);
        toDoList.taskCompleted(0);
        assertEquals(4, toDoList.getFinishedTasks().size());
    }

    @Test
    void testLength() {
        assertEquals(0, toDoList.length());
        addItemsNotDue();
        assertEquals(13, toDoList.length());
    }

    @Test
    void testIsEmpty() {
        assertTrue(toDoList.isEmpty());
        addItemsNotDue();
        assertFalse(toDoList.isEmpty());
    }

    // There is no way to test print statements. I will just call them to include them in code coverage
    @Test
    void testPrintStatements() {
        addItemsNotDue();
        toDoList.printNumberedList();
        toDoList.printTasksByUrgencyAndImportance();
        toDoList.printTasksByTag();
    }

    // EFFECTS: Adds 13 items
    void addItemsNotDue() {
        // Urgent Important
        toDoList.addTask("Finish CPSC 121 homework", new ArrayList<>(Arrays.asList("University")), true, true);
        toDoList.addTask("Pay university tuition", new ArrayList<>(Arrays.asList("University", "Money")), true, true);
        toDoList.addTask("Read CPSC 121 textbook", new ArrayList<>(Arrays.asList("University")), true, true);

        // Not urgent Important
        toDoList.addTask("Go to the gym", new ArrayList<>(Arrays.asList("Health")), false, true);
        toDoList.addTask("Get dentist appointment", new ArrayList<>(Arrays.asList("Health")), false, true);
        toDoList.addTask("Work", new ArrayList<>(Arrays.asList("Work", "Money")), false, true);
        toDoList.addTask("Study for midterms", new ArrayList<>(Arrays.asList("University")), false, true);
        toDoList.addTask("Hangout with friends", new ArrayList<>(Arrays.asList("Hobbies")), false, true);

        // Urgent Not Important
        toDoList.addTask("Return calls", new ArrayList<>(Arrays.asList("University", "Work")), true, false);

        // Not Urgent Not Important
        toDoList.addTask("Get Milk", new ArrayList<>(Arrays.asList("Home", "Grocery")), false, false);
        toDoList.addTask("Practice guitar", new ArrayList<>(Arrays.asList("Hobbies")), false, false);
        toDoList.addTask("Check email", new ArrayList<>(Arrays.asList("University", "Work")), false, false);
        toDoList.addTask("Buy eggs", new ArrayList<>(Arrays.asList("Home", "Grocery")), false, false);
    }



}
