package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CalorieCounterPanel extends JPanel {
    private JTextArea logArea;
    private JProgressBar progressBar;
    private JTextField calorieGoalField;
    private JLabel remainingCaloriesLabel;
    private int totalCalories;
    private int calorieGoal;
    private Map<String, Integer> foodCalories;
    private JPanel buttonPanel;
    private JScrollPane buttonScrollPane;

    public CalorieCounterPanel() {
        setLayout(new BorderLayout());

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        calorieGoalField = new JTextField("2400", 5);
        remainingCaloriesLabel = new JLabel("Remaining Calories: 2400");

        // Default Calorie Goal
        calorieGoal = 2400;

        foodCalories = new HashMap<>();
        populateFoodCalories();

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Calorie Goal:"));
        topPanel.add(calorieGoalField);
        topPanel.add(remainingCaloriesLabel);

        buttonPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        updateFoodButtons();

        buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel addFoodPanel = new JPanel(new FlowLayout());
        JTextField newFoodField = new JTextField(10);
        JTextField newCaloriesField = new JTextField(5);
        JButton addFoodButton = new JButton("Add Food");

        addFoodPanel.add(new JLabel("Food:"));
        addFoodPanel.add(newFoodField);
        addFoodPanel.add(new JLabel("Calories:"));
        addFoodPanel.add(newCaloriesField);
        addFoodPanel.add(addFoodButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(buttonScrollPane, BorderLayout.WEST);
        add(progressBar, BorderLayout.SOUTH);
        add(addFoodPanel, BorderLayout.EAST);

        calorieGoalField.addActionListener(e -> setCalorieGoal());

        addFoodButton.addActionListener(e -> {
            String newFood = newFoodField.getText().trim();
            String newCaloriesStr = newCaloriesField.getText().trim();
            if (!newFood.isEmpty() && !newCaloriesStr.isEmpty()) {
                try {
                    int newCalories = Integer.parseInt(newCaloriesStr);
                    if (!foodCalories.containsKey(newFood)) {
                        foodCalories.put(newFood, newCalories);
                        updateFoodButtons();
                        newFoodField.setText("");
                        newCaloriesField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "This food already exists.", "Duplicate Food", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for calories.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void populateFoodCalories() {
        foodCalories.put("Eier", 78);
        foodCalories.put("Brot", 80);
        foodCalories.put("Apfel", 95);
        foodCalories.put("Banane", 105);
        foodCalories.put("Milch", 150);
        foodCalories.put("Käse", 113);
        foodCalories.put("Huhn", 165);
        foodCalories.put("Reis", 206);
        foodCalories.put("Kartoffeln", 77);
        foodCalories.put("Karotten", 41);
        foodCalories.put("Tomaten", 18);
        foodCalories.put("Lachs", 208);
        foodCalories.put("Beef", 250);
        foodCalories.put("Brokkoli", 55);
        foodCalories.put("Spinat", 23);
        foodCalories.put("Bohnen", 347);
        foodCalories.put("Linsen", 116);
        foodCalories.put("Joghurt", 59);
        foodCalories.put("Butter", 717);
        foodCalories.put("Olivenöl", 884);
        foodCalories.put("Mandeln", 579);
        foodCalories.put("Walnüsse", 654);
        foodCalories.put("Orangen", 47);
        foodCalories.put("Trauben", 69);
        foodCalories.put("Mango", 60);
        foodCalories.put("Ananas", 50);
        foodCalories.put("Avocado", 160);
        foodCalories.put("Schokolade", 546);
        foodCalories.put("Kekse", 502);
        foodCalories.put("Kuchen", 350);
        foodCalories.put("Pizza", 266);
        foodCalories.put("Pasta", 131);
        foodCalories.put("Quinoa", 120);
        foodCalories.put("Chia Samen", 486);
        foodCalories.put("Leinsamen", 534);
        foodCalories.put("Hirse", 119);
        foodCalories.put("Gerste", 354);
        foodCalories.put("Haferflocken", 68);
        foodCalories.put("Cornflakes", 357);
        foodCalories.put("Müsli", 389);
        foodCalories.put("Blaubeeren", 57);
        foodCalories.put("Erdbeeren", 32);
        foodCalories.put("Himbeeren", 52);
        foodCalories.put("Zitrone", 29);
        foodCalories.put("Kokosnuss", 354);
        foodCalories.put("Zucchini", 17);
        foodCalories.put("Paprika", 31);
        foodCalories.put("Gurke", 16);
        foodCalories.put("Pilze", 22);
        foodCalories.put("Knoblauch", 149);
        foodCalories.put("Ingwer", 80);
        foodCalories.put("Honig", 304);
        foodCalories.put("Zucker", 387);
        foodCalories.put("Salz", 0);
    }

    private void updateFoodButtons() {
        buttonPanel.removeAll();
        for (String food : foodCalories.keySet()) {
            JButton foodButton = new JButton(food);
            foodButton.addActionListener(new FoodButtonListener(food, foodCalories.get(food)));
            buttonPanel.add(foodButton);
        }
        buttonPanel.revalidate();
    }

    private class FoodButtonListener implements ActionListener {
        private String food;
        private int calories;

        public FoodButtonListener(String food, int calories) {
            this.food = food;
            this.calories = calories;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addFood(food, calories);
        }
    }

    private void addFood(String food, int calories) {
        totalCalories += calories;
        updateProgressBar();
        updateRemainingCalories();
        logArea.append(food + " - " + calories + " kcal\n");
    }

    private void setCalorieGoal() {
        try {
            calorieGoal = Integer.parseInt(calorieGoalField.getText());
            updateRemainingCalories();
            updateProgressBar();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the calorie goal.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRemainingCalories() {
        int remainingCalories = calorieGoal - totalCalories;
        remainingCaloriesLabel.setText("Remaining Calories: " + remainingCalories);
    }

    private void updateProgressBar() {
        int progress = calorieGoal > 0 ? (int) ((totalCalories / (double) calorieGoal) * 100) : 0;
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
    }
}
