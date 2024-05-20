package ui;

import javax.swing.*;
import java.awt.*;

public class WorkoutTrackerPanel extends JPanel {
    private JTextField workoutField;
    private JTextArea logArea;
    private JButton addButton;

    public WorkoutTrackerPanel() {
        setLayout(new BorderLayout());

        workoutField = new JTextField(20);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        addButton = new JButton("Add Workout");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Workout:"));
        inputPanel.add(workoutField);
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        addButton.addActionListener(e -> addWorkout());
    }

    private void addWorkout() {
        String workout = workoutField.getText();
        if (!workout.isEmpty()) {
            logArea.append(workout + "\n");
            workoutField.setText("");
        }
    }
}