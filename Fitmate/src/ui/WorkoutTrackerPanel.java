package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class WorkoutTrackerPanel extends JPanel {
    private JTextArea logArea;
    private JTextField repsField;
    private JComboBox<String> exerciseComboBox;
    private JButton addButton;
    private JComboBox<String> dateComboBox;
    private Map<String, Integer> exerciseReps;
    private List<String> dateList;
    private String currentDate;
    private static final String FILE_NAME = "workout_data.txt";
    private static final String EXERCISE_FILE = "exercise_list.txt";
    private JTextField newExerciseField;
    private JButton addNewExerciseButton;
    private List<String> exerciseList;

    public WorkoutTrackerPanel() {
        setLayout(new BorderLayout());

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        repsField = new JTextField(5);
        exerciseComboBox = new JComboBox<>();
        addButton = new JButton("Add Exercise");
        dateComboBox = new JComboBox<>();

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        JPanel exercisePanel = new JPanel(new FlowLayout());
        exercisePanel.add(new JLabel("Exercise:"));
        exercisePanel.add(exerciseComboBox);
        exercisePanel.add(new JLabel("Reps:"));
        exercisePanel.add(repsField);
        exercisePanel.add(addButton);

        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.add(new JLabel("Select Date:"));
        datePanel.add(dateComboBox);

        newExerciseField = new JTextField(10);
        addNewExerciseButton = new JButton("Add New Exercise");

        JPanel addExercisePanel = new JPanel(new FlowLayout());
        addExercisePanel.add(new JLabel("New Exercise:"));
        addExercisePanel.add(newExerciseField);
        addExercisePanel.add(addNewExerciseButton);

        topPanel.add(datePanel);
        topPanel.add(exercisePanel);
        topPanel.add(addExercisePanel);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        //add(addExercisePanel, BorderLayout.SOUTH);

        dateComboBox.addActionListener(e -> loadDateData());
        addButton.addActionListener(e -> addExercise());
        addNewExerciseButton.addActionListener(e -> addNewExercise());

        exerciseReps = new HashMap<>();
        dateList = new ArrayList<>();

        loadExerciseList();
        loadDateList();
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!dateList.contains(currentDate)) {
            dateList.add(currentDate);
        }
        dateComboBox.setSelectedItem(currentDate);
        loadDateData();
    }

    private void loadExerciseList() {
        exerciseList = new ArrayList<>();
        // Load existing exercises from file
        File file = new File(EXERCISE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    exerciseList.add(line.trim());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading exercises: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Populate with default exercises if file does not exist
            String[] defaultExercises = {"Push-ups", "Squats", "Lunges", "Plank", "Burpees", "Crunches", "Leg Raises", "Jumping Jacks", "Mountain Climbers", "Bicep Curls"};
            exerciseList.addAll(Arrays.asList(defaultExercises));
        }
        updateExerciseComboBox();
    }

    private void updateExerciseComboBox() {
        exerciseComboBox.removeAllItems();
        for (String exercise : exerciseList) {
            exerciseComboBox.addItem(exercise);
        }
    }

    private void addExercise() {
        String exercise = (String) exerciseComboBox.getSelectedItem();
        String repsStr = repsField.getText();
        if (!exercise.isEmpty() && !repsStr.isEmpty()) {
            try {
                int reps = Integer.parseInt(repsStr);
                exerciseReps.put(exercise, reps);
                logArea.append(exercise + " - " + reps + " reps\n");
                repsField.setText("");
                saveData();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for reps.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addNewExercise() {
        String newExercise = newExerciseField.getText().trim();
        if (!newExercise.isEmpty() && !exerciseList.contains(newExercise)) {
            exerciseList.add(newExercise);
            exerciseComboBox.addItem(newExercise);
            newExerciseField.setText("");
            saveExerciseList();
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid exercise name that does not already exist.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveExerciseList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXERCISE_FILE))) {
            for (String exercise : exerciseList) {
                writer.write(exercise);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving exercise list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveData() {
        try {
            // Read existing data and update it
            Map<String, String> dataMap = new HashMap<>();
            if (new File(FILE_NAME).exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(";");
                        if (parts.length > 1) {
                            dataMap.put(parts[0], parts[1]);
                        }
                    }
                }
            }

            // Update current date data
            StringBuilder sb = new StringBuilder();
            for (String line : logArea.getText().split("\n")) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 2) {
                        sb.append(parts[0]).append(":").append(parts[1].replace(" reps", "")).append(",");
                    }
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); // Remove the last comma
            }
            dataMap.put(currentDate, sb.toString());

            // Write updated data back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    writer.write(entry.getKey() + ";" + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDateData() {
        String selectedDate = (String) dateComboBox.getSelectedItem();
        if (selectedDate == null || selectedDate.isEmpty()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            logArea.setText("");
            exerciseReps.clear();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(selectedDate)) {
                    String[] parts = line.split(";");
                    if (parts.length > 1) {
                        String[] exerciseEntries = parts[1].split(",");
                        for (String entry : exerciseEntries) {
                            String[] exerciseData = entry.split(":");
                            if (exerciseData.length == 2) {
                                String exercise = exerciseData[0];
                                int reps = Integer.parseInt(exerciseData[1].trim());
                                addExerciseToLog(exercise, reps);
                            }
                        }
                    }
                    break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addExerciseToLog(String exercise, int reps) {
        exerciseReps.put(exercise, reps);
        logArea.append(exercise + " - " + reps + " reps\n");
    }

    private void loadDateList() {
        dateList.clear();

        // Add today's, yesterday's, and the day before yesterday's dates
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 14; i++) { // Change to 14 for the last two weeks
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, -1);
        }

        // Load dates from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 0) {
                    String date = parts[0];
                    if (!dateList.contains(date)) {
                        dateList.add(date);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading date list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateDateComboBox();
    }

    private void updateDateComboBox() {
        dateComboBox.removeAllItems();
        for (String date : dateList) {
            dateComboBox.addItem(date);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Workout Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new WorkoutTrackerPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
