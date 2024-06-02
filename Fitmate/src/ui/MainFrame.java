package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("FitMate");
        setSize(1400, 800);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Calorie Counter", new CalorieCounterPanel());
        tabbedPane.addTab("Workout Tracker", new WorkoutTrackerPanel());

        setContentPane(tabbedPane);
    }
}