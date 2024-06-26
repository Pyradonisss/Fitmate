package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
    private JComboBox<String> dateComboBox;
    private String currentDate;
    private static final String FILE_NAME = "calorie_data.txt";
    private List<String> dateList;

    public CalorieCounterPanel() {
        setLayout(new BorderLayout());

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Arial", Font.PLAIN, 14));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int start = logArea.viewToModel(e.getPoint());
                    try {
                        int end = logArea.getLineEndOffset(logArea.getLineOfOffset(start));
                        String selectedText = logArea.getText(logArea.getLineStartOffset(logArea.getLineOfOffset(start)), end - logArea.getLineStartOffset(logArea.getLineOfOffset(start)));
                        int confirm = JOptionPane.showConfirmDialog(null, "Möchten Sie diesen Eintrag löschen?\n" + selectedText, "Eintrag löschen", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            deleteFood(selectedText.trim());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        calorieGoalField = new JTextField("2400", 5);
        remainingCaloriesLabel = new JLabel("Remaining Calories: 2400");

        // Default Calorie Goal
        calorieGoal = 2400;

        foodCalories = new HashMap<>();
        populateFoodCalories();
        loadFoodData();

        dateList = new ArrayList<>();
        dateComboBox = new JComboBox<>();
        loadDateList(); // loadDateList() after dateComboBox initialization

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Calorie Goal:"));
        topPanel.add(calorieGoalField);
        topPanel.add(remainingCaloriesLabel);
        topPanel.add(new JLabel("Select Date:"));
        topPanel.add(dateComboBox);

        buttonPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        updateFoodButtons();

        buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        buttonScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel addFoodPanel = new JPanel(new FlowLayout());
        JTextField newFoodField = new JTextField(10);
        JTextField newCaloriesField = new JTextField(5);
        JTextField newGramsField = new JTextField(5);
        JButton addFoodButton = new JButton("Add Food");

        addFoodPanel.add(new JLabel("Food:"));
        addFoodPanel.add(newFoodField);
        addFoodPanel.add(new JLabel("Calories per 100g:"));
        addFoodPanel.add(newCaloriesField);
        addFoodPanel.add(new JLabel("Grams:"));
        addFoodPanel.add(newGramsField);
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
            String newGramsStr = newGramsField.getText().trim();
            if (!newFood.isEmpty() && !newCaloriesStr.isEmpty() && !newGramsStr.isEmpty()) {
                try {
                    int newCalories = Integer.parseInt(newCaloriesStr);
                    int newGrams = Integer.parseInt(newGramsStr);
                    if (!foodCalories.containsKey(newFood)) {
                        foodCalories.put(newFood, newCalories);
                        updateFoodButtons();
                        addFood(newFood, (newCalories * newGrams) / 100);
                        newFoodField.setText("");
                        newCaloriesField.setText("");
                        newGramsField.setText("");
                        saveFoodData();
                    } else {
                        JOptionPane.showMessageDialog(this, "This food already exists.", "Duplicate Food", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for calories and grams.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dateComboBox.addActionListener(e -> {
            currentDate = (String) dateComboBox.getSelectedItem();
            if (currentDate != null && !currentDate.isEmpty()) {
                resetData(); // Daten zurücksetzen
                loadDateData(); // Daten für das ausgewählte Datum laden
            }
        });

        // Load the initial data for the current date
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!dateList.contains(currentDate)) {
            dateList.add(currentDate);
        }
        dateComboBox.setSelectedItem(currentDate);
        loadDateData();
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
            JPanel foodPanel = new JPanel(new BorderLayout());
            JButton foodButton = new JButton(food);
            JTextField amountField = new JTextField("0", 2);
            JTextField gramsField = new JTextField("0", 2);
            amountField.setHorizontalAlignment(JTextField.CENTER);
            gramsField.setHorizontalAlignment(JTextField.CENTER);
            foodButton.addActionListener(new FoodButtonListener(food, foodCalories.get(food), amountField, gramsField));
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            inputPanel.add(amountField);
            inputPanel.add(new JLabel(" x "));
            inputPanel.add(gramsField);
            inputPanel.add(new JLabel("g"));
            foodPanel.add(foodButton, BorderLayout.NORTH);
            foodPanel.add(inputPanel, BorderLayout.CENTER);
            buttonPanel.add(foodPanel);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private class FoodButtonListener implements ActionListener {
        private String food;
        private int calories;
        private JTextField amountField;
        private JTextField gramsField;

        public FoodButtonListener(String food, int calories, JTextField amountField, JTextField gramsField) {
            this.food = food;
            this.calories = calories;
            this.amountField = amountField;
            this.gramsField = gramsField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int amount = Integer.parseInt(amountField.getText());
                int grams = Integer.parseInt(gramsField.getText());
                addFood(food, (calories * amount * grams) / 100);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CalorieCounterPanel.this, "Please enter valid numbers for amount and grams.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFood(String food, int calories) {
        totalCalories += calories;
        updateProgressBar();
        updateRemainingCalories();
        logArea.append(food + " - " + calories + " kcal\n");
        saveData(); // Save the data whenever a food item is added
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

    private void saveData() {
        try {
            Map<String, String> dataMap = new HashMap<>();

            // Read existing data and update it
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
                        sb.append(parts[0]).append(":").append(parts[1].replace(" kcal", "")).append(",");
                    }
                }
            }

            // Entferne das letzte Komma
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
                sb.setLength(sb.length() - 1);
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
        resetData();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            totalCalories = 0;
            logArea.setText("");

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(selectedDate)) {
                    String[] parts = line.split(";");
                    if (parts.length > 1) {
                        String[] foodEntries = parts[1].split(",");
                        for (String entry : foodEntries) {
                            String[] foodData = entry.split(":");
                            if (foodData.length == 2) {
                                String food = foodData[0];
                                int calories = Integer.parseInt(foodData[1].replace(" kcal", "").trim());
                                addFoodToLog(food, calories);
                            }
                        }
                    }
                    break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateProgressBar();
        updateRemainingCalories();
    }

    private void addFoodToLog(String food, int calories) {
        totalCalories += calories;
        logArea.append(food + " - " + calories + " kcal\n");
    }

    private void loadDateList() {
        dateList = new ArrayList<>();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Add the current date as default
        dateList.add(dateFormat.format(currentDate));

        // Add the dates for the last two weeks
        for (int i = 1; i < 14; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            dateList.add(dateFormat.format(calendar.getTime()));
        }

        dateComboBox.setModel(new DefaultComboBoxModel<>(dateList.toArray(new String[0])));
    }

    private void saveFoodData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("food_data.txt"))) {
            for (Map.Entry<String, Integer> entry : foodCalories.entrySet()) {
                writer.write(entry.getKey() + ";" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving food data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFoodData() {
        File file = new File("food_data.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 2) {
                        String food = parts[0];
                        int calories = Integer.parseInt(parts[1]);
                        foodCalories.put(food, calories);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading food data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetData() {
        totalCalories = 0;
        calorieGoalField.setText("2400");
        remainingCaloriesLabel.setText("Remaining Calories: 2400");
        progressBar.setValue(0);
        progressBar.setString("0%");
        logArea.setText("");
    }

    private void deleteFood(String logEntry) {
        String[] parts = logEntry.split(" - ");
        if (parts.length == 2) {
            String food = parts[0].trim();
            int calories = Integer.parseInt(parts[1].replace(" kcal", "").trim());
            totalCalories -= calories;
            logArea.setText(logArea.getText().replace(logEntry + "\n", ""));
            updateProgressBar();
            updateRemainingCalories();
            saveData();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Calorie Counter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new CalorieCounterPanel());
        frame.pack();
        frame.setVisible(true);
    }
}

