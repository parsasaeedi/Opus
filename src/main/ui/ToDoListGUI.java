package ui;

import model.Event;
import model.EventLog;
import model.Task;
import model.ToDoList;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.event.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

// ToDoList app with user interface
public class ToDoListGUI extends JPanel implements ListSelectionListener, ActionListener, ItemListener {

    // Fields
    private ToDoList toDoList;
    private JList list;
    private JList tabs;
    private JSplitPane splitPane;
    private JScrollPane toDoListScrollPane;
    private JScrollPane tabsScrollPane;

    private JPanel mainPanel;

    private static final String addTaskString = "Add Task";
    private static final String completeString = "Completed";
    private JButton completeButton;
    private JTextField taskName;

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private static final String JSON_STORE = "./data/toDoList.json";


    // ToDoListGUI Constructor
    @SuppressWarnings("methodlength")
    public ToDoListGUI() {

        super(new BorderLayout());

        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Create the list of tabs and put it in a scroll pane.
        toDoList = new ToDoList();

        // The following is just to have at least 1 task before making the JList
        toDoList.addTask("Task 1", new ArrayList<>(Arrays.asList("Tasks")), false, false);

        // Uncomment the following to add items for demo
        // toDoList.addItemsForDemo();

        tabs = new JList(toDoList.getTags().toArray());
        tabs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabs.setSelectedIndex(0);
        tabs.addListSelectionListener(this);
        tabs.setFont(new Font("Arial",Font.PLAIN,16));
        tabs.setName("tabs");


        tabsScrollPane = new JScrollPane(tabs);
        tabsScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        list = new JList(toDoList.tasksToStringList(toDoList.getByTag(toDoList.getTags().get(0))).toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setFont(new Font("Arial",Font.PLAIN,16));
        list.setName("list");

        toDoListScrollPane = new JScrollPane(list);
        toDoListScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        //Create a split pane with the two scroll panes in it.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tabsScrollPane, toDoListScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        //Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(200, 100);
        tabsScrollPane.setMinimumSize(minimumSize);
        toDoListScrollPane.setMinimumSize(minimumSize);

        //Provide a preferred size for the split pane.
        splitPane.setPreferredSize(new Dimension(700, 500));

        // Add button
        JButton addTaskButton = new JButton(addTaskString);
        AddTaskListener addTaskListener = new AddTaskListener(addTaskButton);
        addTaskButton.setActionCommand(addTaskString);
        addTaskButton.addActionListener(addTaskListener);
        addTaskButton.setEnabled(false);

        // Complete button
        completeButton = new JButton(completeString);
        completeButton.setActionCommand(completeString);
        completeButton.addActionListener(new CompleteListener());

        taskName = new JTextField(10);
        taskName.addActionListener(addTaskListener);
        taskName.getDocument().addDocumentListener(addTaskListener);

        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                BoxLayout.LINE_AXIS));
        buttonPane.add(completeButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(taskName);
        buttonPane.add(addTaskButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

//        add(listScrollPane, BorderLayout.CENTER);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    // Necessary override
    @Override
    public void itemStateChanged(ItemEvent e) {}

    // CompleteListener
    class CompleteListener implements ActionListener {
        @SuppressWarnings("methodlength")
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int listSelectedindex = list.getSelectedIndex();
            int tabSelectedIndex = tabs.getSelectedIndex();
            int numOfTags = toDoList.getTags().size();
//            System.out.println(list.getSelectedValue());
//            int selectedTaskId = ((Task) (list.getSelectedValue())).getId();
            Task taskSelected = toDoList.getByTag((String) tabs.getSelectedValue()).get(listSelectedindex);
            toDoList.taskCompletedById(taskSelected.getId());

//            int size = toDoList.length();
            int toDoListsize = list.getModel().getSize();

            if (toDoListsize == 1) { //Nobody's left, disable completed.
                completeButton.setEnabled(false);
//                tabs.setListData(toDoList.getTags().toArray());
//                list.setListData(new ArrayList().toArray());
                if (numOfTags == tabSelectedIndex + 1) {
//                    tabSelectedIndex -= 2;
                    tabSelectedIndex = toDoList.getTags().size() - 1;
                }

            } else if (toDoListsize > 1) { //Select an index.
                if (listSelectedindex == toDoListsize - 1) {
                    //removed item in last position
                    listSelectedindex--;
                }
            }

            int listSize = toDoList.length();

            if (listSize == 0) {
                tabs.setListData(toDoList.getTags().toArray());
                list.setListData(new ArrayList().toArray());
            } else {
                tabs.setListData(toDoList.getTags().toArray());
                tabs.setSelectedIndex(tabSelectedIndex);
                showTab((String) toDoList.getTags().toArray()[tabs.getSelectedIndex()]);

                list.setSelectedIndex(listSelectedindex);
            }

            list.ensureIndexIsVisible(listSelectedindex);
        }
    }


    // MODIFIES: toDoList
    // EFFECTS: If Add button pressed, adds the task to toDoList and refreshes the GUI
    class AddTaskListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;

        public AddTaskListener(JButton button) {
            this.button = button;
        }

        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
            String name = taskName.getText();

            //User didn't type in a unique name...
            if (name.equals("")) {
                Toolkit.getDefaultToolkit().beep();
                taskName.requestFocusInWindow();
                taskName.selectAll();
                return;
            }

            int index = list.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
//                index++;
            }

            String currentTab = toDoList.getTags().get(tabs.getSelectedIndex());
            toDoList.addTask(taskName.getText(), currentTab);
            //If we just wanted to add to the end, we'd do this:
            //listModel.addElement(employeeName.getText());

            int tabSelectedIndex = tabs.getSelectedIndex();
            tabs.setListData(toDoList.getTags().toArray());
            tabs.setSelectedIndex(tabSelectedIndex);
            showTab((String) toDoList.getTags().toArray()[tabs.getSelectedIndex()]);

            //Reset the text field.
            taskName.requestFocusInWindow();
            taskName.setText("");

            //Select the new item and make it visible.
            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
        }

        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }


    //Listens to the list
    public void valueChanged(ListSelectionEvent e) {
        JList listSelected = (JList)e.getSource();
        if (listSelected.getName().equals("tabs")) {
            int selectedIndex = listSelected.getSelectedIndex();
            if (selectedIndex != -1) {
                showTab((String) toDoList.getTags().toArray()[selectedIndex]);
            }
        } else if (listSelected.getName().equals("list")) {
            if (e.getValueIsAdjusting() == false) {

                if (listSelected.getSelectedIndex() == -1) {
                    //No selection, disable fire button.
                    completeButton.setEnabled(false);

                } else {
                    //Selection, enable the fire button.
                    completeButton.setEnabled(true);
                }
            }
        }
    }


    // Shows the selected tab
    protected void showTab(String tab) {
        list.setListData(toDoList.getByTag(tab).toArray());
    }


    public JSplitPane getSplitPane() {
        return splitPane;
    }


    // Creates the Menu Bar
    @SuppressWarnings("methodlength")
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenu submenu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);
        menuBar.setForeground(Color.white);

        //a group of JMenuItems
        // Save item
        ImageIcon saveIcon = createImageIcon("images/save.png");
        menuItem = new JMenuItem("Save To-Do List", saveIcon);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Save To-Do List");
        menuItem.addActionListener(this);
        menuItem.setName("save");
        menu.add(menuItem);

        // load item
        ImageIcon loadIcon = createImageIcon("images/load.png");
        menuItem = new JMenuItem("Load To-Do List",loadIcon);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load To-Do List");
        menuItem.addActionListener(this);
        menuItem.setName("load");
        menu.add(menuItem);

        // Add tab
        ImageIcon addIcon = createImageIcon("images/add.png");
        menuItem = new JMenuItem("Add tab", addIcon);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Add tab");
        menuItem.addActionListener(this);
        menuItem.setName("addTab");
        menu.add(menuItem);

        return menuBar;
    }

    // Handles the buttons in MenuBar
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if (source.getName().equals("save")) {
            saveToDoList();
        } else if (source.getName().equals("load")) {
            loadToDoList();
        } else if (source.getName().equals("addTab")) {
            addTag();
        }
    }

    // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex + 1);
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
            tabs.setListData(toDoList.getTags().toArray());
            tabs.setSelectedIndex(0);
            showTab((String) toDoList.getTags().toArray()[tabs.getSelectedIndex()]);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: adds tag to tags
    private void addTag() {
        JFrame popUpFrame = new JFrame("PopUpFrame");
        String tabName = JOptionPane.showInputDialog(popUpFrame,
                "What is your tab?", null);
        if (tabName != null && tabName.length() > 0) {
            toDoList.addTag(tabName);
        }

        int index = toDoList.getTags().size() - 1;
        tabs.setListData(toDoList.getTags().toArray());
        tabs.setSelectedIndex(index);
        showTab((String) toDoList.getTags().toArray()[tabs.getSelectedIndex()]);
        list.setSelectedIndex(0);
    }

    // Returns an ImageIcon, or null if the path was invalid.
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ToDoListGUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    @SuppressWarnings("methodlength")
    private static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("SplitPaneDemo");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Log:");
                for (Event event: EventLog.getInstance()) {
                    System.out.println(event.getDescription());
                    System.out.println("---------------------");
                }
                System.exit(0);
            }
        });
//        ToDoListGUI toDoListGUI = new ToDoListGUI();
        JComponent toDoListGUI = new ToDoListGUI();
//        frame.getContentPane().add(toDoListGUI.getSplitPane());
        frame.setContentPane(toDoListGUI);
        frame.setJMenuBar(((ToDoListGUI) toDoListGUI).createMenuBar());
//
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    // Main method
    // Run this to start the GUI
    @SuppressWarnings("methodlength")
    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
//        new ToDoListGUI();
    }
}
