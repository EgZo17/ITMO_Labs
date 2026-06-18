package com.labwork.client.ui;

import com.labwork.client.network.NetworkClient;
import com.labwork.client.utils.LocaleManager;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Locale;

public class AuthFrame extends JFrame implements LocaleManager.LocaleChangeListener {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<String> languageComboBox;
    private NetworkClient networkClient;
    private String currentLogin;
    private String currentPassword;

    public AuthFrame(NetworkClient networkClient) {
        this.networkClient = networkClient;
        this.setTitle(LocaleManager.t("app.title"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        initComponents();
        LocaleManager.addListener(this);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Заголовок
        JLabel titleLabel = new JLabel(LocaleManager.t("auth.title"), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Переключатель языка
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel langLabel = new JLabel(LocaleManager.t("main.lang") + ":");
        mainPanel.add(langLabel, gbc);

        languageComboBox = new JComboBox<>();
        for (Locale locale : LocaleManager.getAvailableLocales()) {
            String displayName = locale.getDisplayLanguage(locale);
            if (locale.getCountry().equals("EC")) {
                displayName = "Español (Ecuador)";
            }
            languageComboBox.addItem(displayName);
        }
        
        int currentIndex = 0;
        Locale current = LocaleManager.getCurrentLocale();
        for (int i = 0; i < LocaleManager.getAvailableLocales().size(); i++) {
            if (LocaleManager.getAvailableLocales().get(i).equals(current)) {
                currentIndex = i;
                break;
            }
        }
        languageComboBox.setSelectedIndex(currentIndex);
        
        languageComboBox.addActionListener(e -> {
            Locale selectedLocale = LocaleManager.getAvailableLocales().get(languageComboBox.getSelectedIndex());
            LocaleManager.setLocale(selectedLocale);
            updateUITexts();
        });
        
        gbc.gridx = 1;
        mainPanel.add(languageComboBox, gbc);

        // Логин
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel loginLabel = new JLabel(LocaleManager.t("auth.login") + ":");
        mainPanel.add(loginLabel, gbc);

        loginField = new JTextField(20);
        gbc.gridy = 3;
        mainPanel.add(loginField, gbc);

        // Пароль
        gbc.gridy = 4;
        JLabel passwordLabel = new JLabel(LocaleManager.t("auth.password") + ":");
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        loginButton = new JButton(LocaleManager.t("auth.loginBtn"));
        loginButton.addActionListener(this::onLogin);
        buttonPanel.add(loginButton);

        registerButton = new JButton(LocaleManager.t("auth.registerBtn"));
        registerButton.addActionListener(this::onRegister);
        buttonPanel.add(registerButton);

        gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        this.add(mainPanel);
    }

    private void onLogin(ActionEvent e) {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                LocaleManager.t("auth.emptyFields"), 
                LocaleManager.t("auth.errorTitle"), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Request request = new Request("login", null);
            request.setLogin(login);
            request.setPassword(password);

            networkClient.send(request);
            Response response = networkClient.receive();

            if (response.isSuccess()) {
                this.currentLogin = login;
                this.currentPassword = password;
                JOptionPane.showMessageDialog(this, 
                    LocaleManager.t("auth.success", login),
                    LocaleManager.t("auth.successTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
                openMainWindow();
            } else {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("auth.serverMsgLoginError"),
                    LocaleManager.t("auth.errorTitle"),
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("auth.networkError") + ": " + ex.getMessage(),
                LocaleManager.t("auth.errorTitle"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegister(ActionEvent e) {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("auth.emptyFields"),
                LocaleManager.t("auth.errorTitle"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Request request = new Request("register", null);
            request.setLogin(login);
            request.setPassword(password);

            networkClient.send(request);
            Response response = networkClient.receive();

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("auth.serverMsgRegSuccess"),
                    LocaleManager.t("auth.successTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("auth.serverMsgRegError"),
                    LocaleManager.t("auth.errorTitle"),
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("auth.networkError") + ": " + ex.getMessage(),
                LocaleManager.t("auth.errorTitle"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainWindow() {
        MainFrame mainFrame = new MainFrame(networkClient, currentLogin, currentPassword);
        mainFrame.setVisible(true);
        this.dispose();
    }

    private void updateUITexts() {
        this.setTitle(LocaleManager.t("app.title"));
        // Пересоздаём компоненты с новыми текстами
        this.getContentPane().removeAll();
        initComponents();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onLocaleChanged(Locale newLocale) {
        SwingUtilities.invokeLater(() -> {
            updateUITexts();
        });
    }
}
