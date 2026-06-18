package com.labwork.client.ui;

import com.labwork.client.network.NetworkClient;
import com.labwork.client.utils.LocaleManager;
import com.labwork.common.models.*;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Locale;

public class EditDialog extends JDialog implements LocaleManager.LocaleChangeListener {
    private final NetworkClient networkClient;
    private final String currentLogin;
    private final String currentPassword;
    private final LabWork existingWork;
    private final String customCommand;

    private JTextField nameField;
    private JTextField coordXField;
    private JTextField coordYField;
    private JTextField minimalPointField;
    private JComboBox<String> difficultyBox;
    private JTextField authorNameField;
    private JTextField authorHeightField;
    private JComboBox<String> eyeColorBox;
    private JTextField locationXField;
    private JTextField locationYField;
    private JTextField locationZField;

    // 👇 Старый конструктор — для обратной совместимости
    public EditDialog(JFrame parent, NetworkClient networkClient, 
                     String login, String password, LabWork existingWork) {
        this(parent, networkClient, login, password, existingWork, null);
    }

    // 👇 Новый конструктор с кастомной командой
    public EditDialog(JFrame parent, NetworkClient networkClient, 
                     String login, String password, LabWork existingWork, String customCommand) {
        super(parent, true); // модальное окно
        this.networkClient = networkClient;
        this.currentLogin = login;
        this.currentPassword = password;
        this.existingWork = existingWork;
        this.customCommand = customCommand;

        if (customCommand != null && existingWork == null) {
            if ("add_if_min".equals(customCommand)) {
                setTitle(LocaleManager.t("cmd.addIfMin"));
            } else {
                setTitle(LocaleManager.t("edit.titleNew"));
            }
        } else {
            setTitle(existingWork == null ? 
                LocaleManager.t("edit.titleNew") : 
                LocaleManager.t("edit.titleEdit"));
        }
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        LocaleManager.addListener(this);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Имя
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel(LocaleManager.t("edit.name") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.7;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        row++;

        // Координаты X, Y
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.25;
        mainPanel.add(new JLabel(LocaleManager.t("edit.coordX") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0.25;
        coordXField = new JTextField(10);
        mainPanel.add(coordXField, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1; gbc.weightx = 0.25;
        mainPanel.add(new JLabel(LocaleManager.t("edit.coordY") + ":"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.25;
        coordYField = new JTextField(10);
        mainPanel.add(coordYField, gbc);
        row++;

        // Минимальный балл
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel(LocaleManager.t("edit.minimalPoint") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        minimalPointField = new JTextField(10);
        mainPanel.add(minimalPointField, gbc);
        row++;

        // Сложность
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel(LocaleManager.t("edit.difficulty") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        difficultyBox = new JComboBox<>();
        for (Difficulty d : Difficulty.values()) {
            difficultyBox.addItem(d.toString());
        }
        mainPanel.add(difficultyBox, gbc);
        row++;

        // Имя автора
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel(LocaleManager.t("edit.authorName") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        authorNameField = new JTextField(20);
        mainPanel.add(authorNameField, gbc);
        row++;

        // Рост автора
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel(LocaleManager.t("edit.authorHeight") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        authorHeightField = new JTextField(10);
        mainPanel.add(authorHeightField, gbc);
        row++;

        // Цвет глаз
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        mainPanel.add(new JLabel(LocaleManager.t("edit.eyeColor") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        eyeColorBox = new JComboBox<>();
        for (com.labwork.common.models.Color c : com.labwork.common.models.Color.values()) {
            eyeColorBox.addItem(c.toString());
        }
        mainPanel.add(eyeColorBox, gbc);
        row++;

        // Местоположение X, Y, Z
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.2;
        mainPanel.add(new JLabel(LocaleManager.t("edit.locationX") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 0.25;
        locationXField = new JTextField(10);
        mainPanel.add(locationXField, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1; gbc.weightx = 0.2;
        mainPanel.add(new JLabel(LocaleManager.t("edit.locationY") + ":"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.25;
        locationYField = new JTextField(10);
        mainPanel.add(locationYField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.2;
        mainPanel.add(new JLabel(LocaleManager.t("edit.locationZ") + ":"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.7;
        locationZField = new JTextField(10);
        mainPanel.add(locationZField, gbc);
        row++;

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton(LocaleManager.t("edit.save"));
        saveButton.addActionListener(this::onSave);
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton(LocaleManager.t("edit.cancel"));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4; gbc.weightx = 1.0;
        mainPanel.add(buttonPanel, gbc);

        // Если редактируем — заполняем поля
        if (existingWork != null) {
            fillFields();
        }

        this.add(mainPanel);
    }

    private void fillFields() {
        nameField.setText(existingWork.getName());
        coordXField.setText(String.valueOf(existingWork.getCoordinates().getX()));
        coordYField.setText(String.valueOf(existingWork.getCoordinates().getY()));
        
        if (existingWork.getMinimalPoint() != null) {
            minimalPointField.setText(String.valueOf(existingWork.getMinimalPoint()));
        }
        
        if (existingWork.getDifficulty() != null) {
            difficultyBox.setSelectedItem(existingWork.getDifficulty().toString());
        }
        
        if (existingWork.getAuthor() != null) {
            authorNameField.setText(existingWork.getAuthor().getName());
            authorHeightField.setText(String.valueOf(existingWork.getAuthor().getHeight()));
            
            if (existingWork.getAuthor().getEyeColor() != null) {
                eyeColorBox.setSelectedItem(existingWork.getAuthor().getEyeColor().toString());
            }
            
            if (existingWork.getAuthor().getLocation() != null) {
                locationXField.setText(String.valueOf(existingWork.getAuthor().getLocation().getX()));
                locationYField.setText(String.valueOf(existingWork.getAuthor().getLocation().getY()));
                locationZField.setText(String.valueOf(existingWork.getAuthor().getLocation().getZ()));
            }
        }
    }

    private void onSave(ActionEvent e) {
        // Валидация
        if (!validateFields()) {
            return;
        }

        try {
            // Создаём объект LabWork
            LabWork labWork = buildLabWork();

            String command;
            if (customCommand != null) {
                command = customCommand;
            } else {
                command = existingWork == null ? "add" : "update";
            }
            
            Object argument = existingWork == null ? labWork : 
                new Object[]{existingWork.getId(), labWork};

            Request request = new Request(command, argument);
            request.setLogin(currentLogin);
            request.setPassword(currentPassword);

            networkClient.send(request);
            Response response = networkClient.receive();

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                    existingWork == null ? 
                        LocaleManager.t("edit.successAdd") : 
                        LocaleManager.t("edit.successUpdate"),
                    LocaleManager.t("edit.titleNew"),
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    response.getMessage(),
                    LocaleManager.t("edit.validationError"),
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("auth.networkError") + ": " + ex.getMessage(),
                LocaleManager.t("edit.validationError"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields() {
        // Обязательные поля
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.nameRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (coordXField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.coordXRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (coordYField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.coordYRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (authorNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.authorNameRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (authorHeightField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.authorHeightRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (locationXField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.locationXRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (locationYField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.locationYRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (locationZField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.locationZRequired"),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Проверка числовых полей
        try {
            Double.parseDouble(coordXField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.coordX")),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(coordYField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.coordY")),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(authorHeightField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.authorHeight")),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(locationXField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.locationX")),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(locationYField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.locationY")),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(locationZField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.locationZ")),
                LocaleManager.t("edit.validationError"),
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Проверка minimalPoint (может быть пустым, но если введён - должен быть числом)
        if (!minimalPointField.getText().trim().isEmpty()) {
            try {
                Float.parseFloat(minimalPointField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("edit.invalidNumber", LocaleManager.t("edit.minimalPoint")),
                    LocaleManager.t("edit.validationError"),
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private LabWork buildLabWork() {
        double coordX = Double.parseDouble(coordXField.getText().trim());
        int coordY = Integer.parseInt(coordYField.getText().trim());
        Coordinates coordinates = new Coordinates(coordX, coordY);

        Float minimalPoint = minimalPointField.getText().trim().isEmpty() ? 
            null : Float.parseFloat(minimalPointField.getText().trim());

        Difficulty difficulty = difficultyBox.getSelectedItem() != null ? 
            Difficulty.valueOf(difficultyBox.getSelectedItem().toString()) : null;

        String authorName = authorNameField.getText().trim();
        double authorHeight = Double.parseDouble(authorHeightField.getText().trim());
        
        // Используем полное имя для enum Color
        com.labwork.common.models.Color eyeColor = eyeColorBox.getSelectedItem() != null ? 
            com.labwork.common.models.Color.valueOf(eyeColorBox.getSelectedItem().toString()) : null;

        int locX = Integer.parseInt(locationXField.getText().trim());
        double locY = Double.parseDouble(locationYField.getText().trim());
        double locZ = Double.parseDouble(locationZField.getText().trim());
        Location location = new Location(locX, locY, locZ);

        Person author = new Person(authorName, authorHeight, eyeColor, location);

        return new LabWork(
            existingWork != null ? existingWork.getId() : 0,
            nameField.getText().trim(),
            coordinates,
            minimalPoint,
            difficulty,
            author
        );
    }

    @Override
    public void onLocaleChanged(Locale newLocale) {
        SwingUtilities.invokeLater(() -> {
            // Пересоздаём окно с новыми текстами
            this.getContentPane().removeAll();
            
            // Обновляем заголовок с учётом кастомной команды
            if (customCommand != null && existingWork == null) {
                if ("add_if_min".equals(customCommand)) {
                    setTitle(LocaleManager.t("cmd.addIfMin"));
                } else {
                    setTitle(LocaleManager.t("edit.titleNew"));
                }
            } else {
                setTitle(existingWork == null ? 
                    LocaleManager.t("edit.titleNew") : 
                    LocaleManager.t("edit.titleEdit"));
            }
            
            initComponents();
            this.revalidate();
            this.repaint();
        });
    }
}
