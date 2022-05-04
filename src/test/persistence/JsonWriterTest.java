package persistence;

import model.ToDoList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {
    //NOTE TO CPSC 210 STUDENTS: the strategy in designing tests for the JsonWriter is to
    //write data to a file and then use the reader to read it back in and check that we
    //read in a copy of what was written out.

    @Test
    void testWriterInvalidFile() {
        try {
            ToDoList toDoList = new ToDoList();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyToDoList() {
        try {
            ToDoList toDoList = new ToDoList();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyToDoList.json");
            writer.open();
            writer.write(toDoList);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyToDoList.json");
            toDoList = reader.read();
            assertEquals(0, toDoList.length());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            ToDoList toDoList = new ToDoList();
            toDoList.addTask("Finish CPSC 121 homework", new ArrayList<>(Arrays.asList("University")), true, true);
            toDoList.addTask("Pay university tuition", new ArrayList<>(Arrays.asList("University", "Money")), true, true);
            toDoList.addTask("Read CPSC 121 textbook", new ArrayList<>(Arrays.asList("University")), true, true);
            toDoList.addTask("Go to the gym", new ArrayList<>(Arrays.asList("Health")), false, true);
            toDoList.taskCompleted(3);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralToDoList.json");
            writer.open();
            writer.write(toDoList);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralToDoList.json");
            toDoList = reader.read();
            assertEquals(3, toDoList.length());
            assertEquals("Finish CPSC 121 homework", toDoList.get(0).getTask());
            assertEquals("Pay university tuition", toDoList.get(1).getTask());
            assertEquals("Read CPSC 121 textbook", toDoList.get(2).getTask());
            assertEquals(2, toDoList.getTags().size());
            assertEquals(1, toDoList.getFinishedTasks().size());
            assertEquals("Go to the gym", toDoList.getFinishedTasks().get(0).getTask());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}