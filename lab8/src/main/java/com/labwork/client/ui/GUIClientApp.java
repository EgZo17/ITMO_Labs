package com.labwork.client.ui;

import com.labwork.client.network.NetworkClient;

import javax.swing.*;
import java.io.IOException;

/**
 * Точка входа для GUI-клиента.
 */
public class GUIClientApp {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            NetworkClient networkClient = connectWithRetry();
            if (networkClient == null) {
                System.exit(1);
            }

            AuthFrame authFrame = new AuthFrame(networkClient);
            authFrame.setVisible(true);
        });
    }

    private static NetworkClient connectWithRetry() {
        while (true) {
            try {
                return new NetworkClient(HOST, PORT);
            } catch (IOException e) {
                int result = JOptionPane.showConfirmDialog(
                    null,
                    "Server is unavailable at " + HOST + ":" + PORT + "\n\n" +
                    "Error: " + e.getMessage() + "\n\nRetry connection?",
                    "Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );

                if (result != JOptionPane.YES_OPTION) {
                    return null;
                }
            }
        }
    }
}
