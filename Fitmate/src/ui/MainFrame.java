package ui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("FitMate");
        setSize(800, 600);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Calorie Counter", new CalorieCounterPanel());
        tabbedPane.addTab("Workout Tracker", new WorkoutTrackerPanel());

        setContentPane(tabbedPane);
    }
}