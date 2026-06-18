package com.labwork.client.ui;

import com.labwork.client.network.NetworkClient;
import com.labwork.client.utils.LocaleManager;
import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame implements LocaleManager.LocaleChangeListener {
    private final NetworkClient networkClient;
    private final String currentLogin;
    private final String currentPassword;

    private javax.swing.Timer pollingTimer;
    private int lastCollectionHash = 0;

    private boolean isUpdating = false;
    private List<LabWork> allWorks = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> filterColumnBox;
    private JComboBox<String> sortColumnBox;
    private JTextField filterValueField;
    private VisualizationPanel visPanel;
    private JLabel userLabel;
    private JCheckBox reverseCheckBox;
    private JMenuBar menuBar;
    
    private JLabel filterLabel;
    private JButton applyFilterBtn;
    private JLabel sortLabel;
    private JMenu langMenu;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton logoutButton;

    private String[] columnKeys = {
        "table.id", "table.name", "table.coordX", "table.coordY",
        "table.creationDate", "table.minimalPoint", "table.difficulty",
        "table.authorName", "table.authorHeight", "table.eyeColor",
        "table.locX", "table.locY", "table.locZ", "table.ownerLogin"
    };

    public MainFrame(NetworkClient networkClient, String login, String password) {
        this.networkClient = networkClient;
        this.currentLogin = login;
        this.currentPassword = password;

        setTitle(LocaleManager.t("app.title") + " - " + currentLogin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 700);
        setLocationRelativeTo(null);

        initComponents();
        LocaleManager.addListener(this);
        loadDataFromServer();

        lastCollectionHash = calculateCollectionHash(
            new LinkedList<>(allWorks)
        );
        startPolling();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopPolling();
            }
        });
    }

    private void initComponents() {
        // Верхняя панель с меню и кнопками
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Меню языка слева
        menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        
        langMenu = new JMenu(LocaleManager.t("main.lang"));
        for (Locale locale : LocaleManager.getAvailableLocales()) {
            String name = locale.getDisplayLanguage(locale);
            if (locale.getCountry().equals("EC")) name = "Español (Ecuador)";
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(e -> LocaleManager.setLocale(locale));
            langMenu.add(item);
        }
        menuBar.add(langMenu);
        topPanel.add(menuBar, BorderLayout.WEST);

        // Кнопки действий
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        addButton = new JButton(LocaleManager.t("main.add"));
        addButton.addActionListener(e -> openEditDialog(null));
        buttonPanel.add(addButton);

        editButton = new JButton(LocaleManager.t("main.update"));
        editButton.addActionListener(e -> {
            LabWork selected = getSelectedLabWork();
            if (selected != null) {
                openEditDialog(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("main.selectObjectEdit"),
                    LocaleManager.t("auth.errorTitle"),
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(editButton);

        deleteButton = new JButton(LocaleManager.t("main.delete"));
        deleteButton.addActionListener(e -> deleteSelectedLabWork());
        buttonPanel.add(deleteButton);

        refreshButton = new JButton(LocaleManager.t("main.refresh"));
        refreshButton.addActionListener(e -> loadDataFromServer());
        buttonPanel.add(refreshButton);

        logoutButton = new JButton(LocaleManager.t("main.logout"));
        logoutButton.addActionListener(e -> {
            stopPolling();
            this.dispose();
            new AuthFrame(networkClient).setVisible(true);
        });
        buttonPanel.add(logoutButton);

        JMenu commandsMenu = new JMenu(LocaleManager.t("main.commands"));
        commandsMenu.setName("commandsMenu");

        // AddIfMin
        JMenuItem addIfMinItem = new JMenuItem(LocaleManager.t("cmd.addIfMin"));
        addIfMinItem.addActionListener(e -> executeAddIfMin());
        commandsMenu.add(addIfMinItem);

        // Clear
        JMenuItem clearItem = new JMenuItem(LocaleManager.t("cmd.clear"));
        clearItem.addActionListener(e -> executeClear());
        commandsMenu.add(clearItem);

        // Info
        JMenuItem infoItem = new JMenuItem(LocaleManager.t("cmd.info"));
        infoItem.addActionListener(e -> executeInfo());
        commandsMenu.add(infoItem);

        // ServerInfo
        JMenuItem serverInfoItem = new JMenuItem(LocaleManager.t("cmd.serverInfo"));
        serverInfoItem.addActionListener(e -> executeServerInfo());
        commandsMenu.add(serverInfoItem);

        // RemoveLower
        JMenuItem removeLowerItem = new JMenuItem(LocaleManager.t("cmd.removeLower"));
        removeLowerItem.addActionListener(e -> executeRemoveLower());
        commandsMenu.add(removeLowerItem);

        // RemoveAnyByAuthor
        JMenuItem removeAnyByAuthorItem = new JMenuItem(LocaleManager.t("cmd.removeAnyByAuthor"));
        removeAnyByAuthorItem.addActionListener(e -> executeRemoveAnyByAuthor());
        commandsMenu.add(removeAnyByAuthorItem);

        menuBar.add(commandsMenu);
        topPanel.add(menuBar, BorderLayout.WEST);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);

        // Центральная часть: SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);

        // Левая часть: Таблица с фильтрами
        JPanel leftPanel = new JPanel(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filterLabel = new JLabel(LocaleManager.t("main.filterLabel") + ":");
        controlPanel.add(filterLabel);
        
        filterColumnBox = new JComboBox<>(getTranslatedColumns());
        filterValueField = new JTextField(10);
        
        applyFilterBtn = new JButton(LocaleManager.t("main.apply"));
        applyFilterBtn.addActionListener(e -> applyStreamsFilterAndSort());
        
        controlPanel.add(filterColumnBox);
        controlPanel.add(filterValueField);
        controlPanel.add(applyFilterBtn);
        
        sortLabel = new JLabel(LocaleManager.t("main.sort") + ":");
        controlPanel.add(sortLabel);
        
        sortColumnBox = new JComboBox<>(getTranslatedColumns());
        sortColumnBox.addActionListener(e -> {
            if (!isUpdating) {
                applyStreamsFilterAndSort();
            }
        });
        controlPanel.add(sortColumnBox);

        reverseCheckBox = new JCheckBox("↓");
        reverseCheckBox.setToolTipText(LocaleManager.t("main.reverse"));
        reverseCheckBox.addActionListener(e -> applyStreamsFilterAndSort());
        controlPanel.add(reverseCheckBox);
        
        leftPanel.add(controlPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(getTranslatedColumns());
        table = new JTable(tableModel);
        leftPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Правая часть: Визуализация
        visPanel = new VisualizationPanel();
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(visPanel);

        this.add(splitPane, BorderLayout.CENTER);

        // Нижняя панель
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userLabel = new JLabel(LocaleManager.t("main.user") + ": " + currentLogin);
        bottomPanel.add(userLabel);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private LabWork getSelectedLabWork() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return null;
        
        // Получаем ID из первой колонки таблицы
        Object idObj = tableModel.getValueAt(selectedRow, 0);
        if (idObj == null) return null;
        
        int id;
        try {
            id = Integer.parseInt(idObj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
        
        // Ищем объект в allWorks по ID
        for (LabWork w : allWorks) {
            if (w.getId() == id) {
                return w;
            }
        }
        return null;
    }

    private void openEditDialog(LabWork labWork) {
        if (labWork == null) {
            EditDialog dialog = new EditDialog(this, networkClient, 
                currentLogin, currentPassword, null);
            dialog.setVisible(true);
            loadDataFromServer();
            return;
        }
        
        // Проверку прав убираем — сервер сам проверит и вернёт ошибку, если что
        EditDialog dialog = new EditDialog(this, networkClient, 
            currentLogin, currentPassword, labWork);
        dialog.setVisible(true);
        loadDataFromServer();
    }

    private void deleteSelectedLabWork() {
        LabWork selected = getSelectedLabWork();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("main.selectObjectDelete"),
                LocaleManager.t("auth.errorTitle"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] options = {
            LocaleManager.t("dialog.yes"),
            LocaleManager.t("dialog.no")
        };
        
        int confirm = JOptionPane.showOptionDialog(this,
            LocaleManager.t("main.confirmDelete", selected.getName()),
            LocaleManager.t("main.delete"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]
        );

        if (confirm == 0) { // 0 = первая кнопка (Да)
            try {
                Request request = new Request("remove_by_id", selected.getId());
                request.setLogin(currentLogin);
                request.setPassword(currentPassword);

                networkClient.send(request);
                Response response = networkClient.receive();

                if (response.isSuccess()) {
                    boolean removed = allWorks.removeIf(w -> w.getId() == selected.getId());
                    System.out.println("DEBUG: Object removed from local list: " + removed);
                    
                    visPanel.setWorks(allWorks);
                    applyStreamsFilterAndSort();
                    tableModel.fireTableDataChanged();
                    table.repaint();
                } else {
                    JOptionPane.showMessageDialog(this,
                        localizeErrorMessage(response.getMessage()),
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
    }

    private String[] getTranslatedColumns() {
        String[] cols = new String[columnKeys.length];
        for (int i = 0; i < columnKeys.length; i++) {
            cols[i] = LocaleManager.t(columnKeys[i]);
        }
        return cols;
    }

    private void loadDataFromServer() {
        try {
            Request req = new Request("show", null);
            req.setLogin(currentLogin);
            req.setPassword(currentPassword);
            networkClient.send(req);
            Response resp = networkClient.receive();

            if (resp.isSuccess() && resp.getData() != null) {
                allWorks = new ArrayList<>();
                for (Object obj : resp.getData()) {
                    if (obj instanceof LabWork) {
                        allWorks.add((LabWork) obj);
                    }
                }
                visPanel.setWorks(allWorks);
                applyStreamsFilterAndSort();
            } else {
                JOptionPane.showMessageDialog(this, resp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Network error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyStreamsFilterAndSort() {
        String filterCol = (String) filterColumnBox.getSelectedItem();
        String sortCol = (String) sortColumnBox.getSelectedItem();
        String filterVal = filterValueField.getText().trim().toLowerCase();
        boolean reverse = reverseCheckBox != null && reverseCheckBox.isSelected();

        List<LabWork> processed = allWorks.stream()
            .filter(work -> {
                if (filterVal.isEmpty()) return true;
                String cellValue = getCellValueAsString(work, filterCol).toLowerCase();
                return cellValue.contains(filterVal);
            })
            .sorted((w1, w2) -> {
                // Определяем тип колонки и сортируем правильно
                int result = compareByColumn(w1, w2, sortCol);
                return reverse ? -result : result;
            })
            .collect(Collectors.toList());

        updateTableModel(processed);
    }

    // Новый метод для правильного сравнения
    private int compareByColumn(LabWork w1, LabWork w2, String columnName) {
        // Числовые поля
        if (columnName.equals(LocaleManager.t("table.id"))) {
            return Integer.compare(w1.getId(), w2.getId());
        }
        if (columnName.equals(LocaleManager.t("table.coordX"))) {
            return Double.compare(w1.getCoordinates().getX(), w2.getCoordinates().getX());
        }
        if (columnName.equals(LocaleManager.t("table.coordY"))) {
            return Integer.compare(w1.getCoordinates().getY(), w2.getCoordinates().getY());
        }
        if (columnName.equals(LocaleManager.t("table.minimalPoint"))) {
            return Float.compare(
                w1.getMinimalPoint() != null ? w1.getMinimalPoint() : Float.MAX_VALUE,
                w2.getMinimalPoint() != null ? w2.getMinimalPoint() : Float.MAX_VALUE
            );
        }
        if (columnName.equals(LocaleManager.t("table.authorHeight"))) {
            double h1 = w1.getAuthor() != null ? w1.getAuthor().getHeight() : 0;
            double h2 = w2.getAuthor() != null ? w2.getAuthor().getHeight() : 0;
            return Double.compare(h1, h2);
        }
        if (columnName.equals(LocaleManager.t("table.locX"))) {
            int x1 = w1.getAuthor() != null && w1.getAuthor().getLocation() != null ? w1.getAuthor().getLocation().getX() : 0;
            int x2 = w2.getAuthor() != null && w2.getAuthor().getLocation() != null ? w2.getAuthor().getLocation().getX() : 0;
            return Integer.compare(x1, x2);
        }
        if (columnName.equals(LocaleManager.t("table.locY"))) {
            double y1 = w1.getAuthor() != null && w1.getAuthor().getLocation() != null ? w1.getAuthor().getLocation().getY() : 0;
            double y2 = w2.getAuthor() != null && w2.getAuthor().getLocation() != null ? w2.getAuthor().getLocation().getY() : 0;
            return Double.compare(y1, y2);
        }
        if (columnName.equals(LocaleManager.t("table.locZ"))) {
            double z1 = w1.getAuthor() != null && w1.getAuthor().getLocation() != null ? w1.getAuthor().getLocation().getZ() : 0;
            double z2 = w2.getAuthor() != null && w2.getAuthor().getLocation() != null ? w2.getAuthor().getLocation().getZ() : 0;
            return Double.compare(z1, z2);
        }
        
        // Для дат
        if (columnName.equals(LocaleManager.t("table.creationDate"))) {
            if (w1.getCreationDate() == null && w2.getCreationDate() == null) return 0;
            if (w1.getCreationDate() == null) return 1;
            if (w2.getCreationDate() == null) return -1;
            return w1.getCreationDate().compareTo(w2.getCreationDate());
        }
        
        // Для перечислений
        if (columnName.equals(LocaleManager.t("table.difficulty"))) {
            String d1 = w1.getDifficulty() != null ? w1.getDifficulty().toString() : "";
            String d2 = w2.getDifficulty() != null ? w2.getDifficulty().toString() : "";
            return d1.compareTo(d2);
        }
        if (columnName.equals(LocaleManager.t("table.eyeColor"))) {
            String c1 = w1.getAuthor() != null && w1.getAuthor().getEyeColor() != null ? w1.getAuthor().getEyeColor().toString() : "";
            String c2 = w2.getAuthor() != null && w2.getAuthor().getEyeColor() != null ? w2.getAuthor().getEyeColor().toString() : "";
            return c1.compareTo(c2);
        }
        
        // По умолчанию - строковое сравнение
        String v1 = getCellValueAsString(w1, columnName);
        String v2 = getCellValueAsString(w2, columnName);
        return v1.compareTo(v2);
    }

    private String getCellValueAsString(LabWork work, String columnName) {
        if (columnName.equals(LocaleManager.t("table.name"))) return work.getName();
        if (columnName.equals(LocaleManager.t("table.difficulty"))) return work.getDifficulty() != null ? work.getDifficulty().toString() : "";
        if (columnName.equals(LocaleManager.t("table.authorName"))) return work.getAuthor() != null ? work.getAuthor().getName() : "";
        if (columnName.equals(LocaleManager.t("table.eyeColor"))) return work.getAuthor() != null && work.getAuthor().getEyeColor() != null ? work.getAuthor().getEyeColor().toString() : "";
        if (columnName.equals(LocaleManager.t("table.minimalPoint"))) return work.getMinimalPoint() != null ? work.getMinimalPoint().toString() : "0";
        if (columnName.equals(LocaleManager.t("table.coordX"))) return String.valueOf(work.getCoordinates().getX());
        if (columnName.equals(LocaleManager.t("table.coordY"))) return String.valueOf(work.getCoordinates().getY());
        if (columnName.equals(LocaleManager.t("table.id"))) return String.valueOf(work.getId());
        if (columnName.equals(LocaleManager.t("table.ownerLogin"))) return work.getOwnerLogin() != null ? work.getOwnerLogin() : "";
        if (columnName.equals(LocaleManager.t("table.creationDate"))) return work.getCreationDate() != null ? work.getCreationDate().toString() : "";
        
        if (columnName.equals(LocaleManager.t("table.locX"))) {
            if (work.getAuthor() != null && work.getAuthor().getLocation() != null) {
                return String.valueOf(work.getAuthor().getLocation().getX());
            }
            return "0";
        }
        if (columnName.equals(LocaleManager.t("table.locY"))) {
            if (work.getAuthor() != null && work.getAuthor().getLocation() != null) {
                return String.valueOf(work.getAuthor().getLocation().getY());
            }
            return "0";
        }
        if (columnName.equals(LocaleManager.t("table.locZ"))) {
            if (work.getAuthor() != null && work.getAuthor().getLocation() != null) {
                return String.valueOf(work.getAuthor().getLocation().getZ());
            }
            return "0";
        }
        
        // Для authorHeight
        if (columnName.equals(LocaleManager.t("table.authorHeight"))) {
            if (work.getAuthor() != null) {
                return String.valueOf(work.getAuthor().getHeight());
            }
            return "0";
        }
        
        return "";
    }

    private void updateTableModel(List<LabWork> works) {
        tableModel.setRowCount(0);
        for (LabWork w : works) {
            tableModel.addRow(new Object[]{
                w.getId(), 
                w.getName(), 
                w.getCoordinates().getX(), 
                w.getCoordinates().getY(),
                w.getCreationDate(), 
                w.getMinimalPoint(), 
                w.getDifficulty(),
                w.getAuthor() != null ? w.getAuthor().getName() : "",
                w.getAuthor() != null ? w.getAuthor().getHeight() : 0,
                w.getAuthor() != null && w.getAuthor().getEyeColor() != null ? w.getAuthor().getEyeColor() : "",
                w.getAuthor() != null && w.getAuthor().getLocation() != null ? w.getAuthor().getLocation().getX() : 0,
                w.getAuthor() != null && w.getAuthor().getLocation() != null ? w.getAuthor().getLocation().getY() : 0,
                w.getAuthor() != null && w.getAuthor().getLocation() != null ? w.getAuthor().getLocation().getZ() : 0,
                w.getOwnerId() != null ? w.getOwnerLogin() : ""
            });
        }
    }

    private void startPolling() {
        if (pollingTimer != null) {
            pollingTimer.stop();
        }

        // Создаем таймер, который срабатывает каждые 3000 мс (3 секунды)
        pollingTimer = new javax.swing.Timer(3000, e -> checkForUpdates());
        pollingTimer.start();
    }

    private void stopPolling() {
        if (pollingTimer != null) {
            pollingTimer.stop();
            pollingTimer = null;
        }
    }

    private void checkForUpdates() {
        // Запускаем сетевой запрос в отдельном потоке, 
        // чтобы интерфейс не зависал на время ожидания ответа от сервера
        new Thread(() -> {
            try {
                Request req = new Request("show", null);
                req.setLogin(currentLogin);
                req.setPassword(currentPassword);
                networkClient.send(req);
                Response resp = networkClient.receive();

                if (resp.isSuccess() && resp.getData() != null) {
                    int newHash = calculateCollectionHash(resp.getData());
                    
                    // Если хеш изменился — данные на сервере обновились
                    if (newHash != lastCollectionHash) {
                        lastCollectionHash = newHash;
                        
                        // Возвращаемся в главный поток (EDT) для обновления интерфейса
                        SwingUtilities.invokeLater(() -> {
                            allWorks.clear();
                            for (Object obj : resp.getData()) {
                                if (obj instanceof LabWork) {
                                    allWorks.add((LabWork) obj);
                                }
                            }
                            visPanel.setWorks(allWorks);
                            applyStreamsFilterAndSort();
                        });
                    }
                }
            } catch (Exception ex) {
                // Тихо игнорируем ошибки сети (например, если сервер временно недоступен)
            }
        }).start();
    }

    private int calculateCollectionHash(LinkedList<?> data) {
        int hash = 0;
        for (Object obj : data) {
            if (obj instanceof LabWork) {
                LabWork w = (LabWork) obj;
                hash = hash * 31 + w.getId();
                if (w.getCreationDate() != null) {
                    hash = hash * 31 + w.getCreationDate().hashCode();
                }
            }
        }
        return hash;
    }

    private void executeAddIfMin() {
        EditDialog dialog = new EditDialog(this, networkClient, 
            currentLogin, currentPassword, null, "add_if_min");
        dialog.setTitle(LocaleManager.t("cmd.addIfMin"));
        dialog.setVisible(true);
        loadDataFromServer();
    }

    private void executeClear() {
        int confirm = JOptionPane.showConfirmDialog(this,
            LocaleManager.t("cmd.confirmClear"),
            LocaleManager.t("cmd.clear"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Request request = new Request("clear", null);
                request.setLogin(currentLogin);
                request.setPassword(currentPassword);

                networkClient.send(request);
                Response response = networkClient.receive();

                if (response.isSuccess()) {
                    loadDataFromServer();
                    JOptionPane.showMessageDialog(this,
                        response.getMessage(),
                        LocaleManager.t("main.success"),
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        localizeErrorMessage(response.getMessage()),
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
    }

    private void executeInfo() {
        try {
            Request request = new Request("info", null);
            request.setLogin(currentLogin);
            request.setPassword(currentPassword);

            networkClient.send(request);
            Response response = networkClient.receive();

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                    response.getMessage(),
                    LocaleManager.t("cmd.info"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("auth.networkError") + ": " + ex.getMessage(),
                LocaleManager.t("auth.errorTitle"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeServerInfo() {
        try {
            Request request = new Request("server_info", null);
            request.setLogin(currentLogin);
            request.setPassword(currentPassword);

            networkClient.send(request);
            Response response = networkClient.receive();

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                    response.getMessage(),
                    LocaleManager.t("cmd.serverInfo"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                LocaleManager.t("auth.networkError") + ": " + ex.getMessage(),
                LocaleManager.t("auth.errorTitle"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeRemoveLower() {
        String input = JOptionPane.showInputDialog(
            this,
            LocaleManager.t("cmd.enterMinimalPoint"),
            LocaleManager.t("cmd.removeLower"),
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                Float minimalPoint = Float.parseFloat(input.trim());
                
                Request request = new Request("remove_lower", minimalPoint);
                request.setLogin(currentLogin);
                request.setPassword(currentPassword);

                networkClient.send(request);
                Response response = networkClient.receive();

                if (response.isSuccess()) {
                    loadDataFromServer();
                    JOptionPane.showMessageDialog(this,
                        response.getMessage(),
                        LocaleManager.t("main.success"),
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        localizeErrorMessage(response.getMessage()),
                        LocaleManager.t("auth.errorTitle"),
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("edit.invalidNumber", "Minimal Point"),
                    LocaleManager.t("edit.validationError"),
                    JOptionPane.WARNING_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                    LocaleManager.t("auth.networkError") + ": " + ex.getMessage(),
                    LocaleManager.t("auth.errorTitle"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void executeRemoveAnyByAuthor() {
        String authorName = JOptionPane.showInputDialog(
            this,
            LocaleManager.t("cmd.enterAuthorName"),
            LocaleManager.t("cmd.removeAnyByAuthor"),
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (authorName != null && !authorName.trim().isEmpty()) {
            try {
                Request request = new Request("remove_any_by_author", authorName.trim());
                request.setLogin(currentLogin);
                request.setPassword(currentPassword);

                networkClient.send(request);
                Response response = networkClient.receive();

                if (response.isSuccess()) {
                    loadDataFromServer();
                    JOptionPane.showMessageDialog(this,
                        response.getMessage(),
                        LocaleManager.t("main.success"),
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        localizeErrorMessage(response.getMessage()),
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
    }

    @Override
    public void onLocaleChanged(Locale newLocale) {
        SwingUtilities.invokeLater(() -> {
            isUpdating = true;
            
            // Сохраняем текущие индексы и значение фильтра
            int currentFilterIndex = filterColumnBox.getSelectedIndex();
            int currentSortIndex = sortColumnBox.getSelectedIndex();
            String currentFilterValue = filterValueField.getText();
            boolean currentReverse = reverseCheckBox != null && reverseCheckBox.isSelected();
            
            // Обновляем заголовок
            setTitle(LocaleManager.t("app.title") + " - " + currentLogin);
            
            // Обновляем меню
            langMenu.setText(LocaleManager.t("main.lang"));

            if (menuBar != null && menuBar.getMenuCount() > 1) {
                JMenu cmdsMenu = menuBar.getMenu(1);
                cmdsMenu.setText(LocaleManager.t("main.commands"));
                
                if (cmdsMenu.getItemCount() >= 6) {
                    cmdsMenu.getItem(0).setText(LocaleManager.t("cmd.addIfMin"));
                    cmdsMenu.getItem(1).setText(LocaleManager.t("cmd.clear"));
                    cmdsMenu.getItem(2).setText(LocaleManager.t("cmd.info"));
                    cmdsMenu.getItem(3).setText(LocaleManager.t("cmd.serverInfo"));
                    cmdsMenu.getItem(4).setText(LocaleManager.t("cmd.removeLower"));
                    cmdsMenu.getItem(5).setText(LocaleManager.t("cmd.removeAnyByAuthor"));
                }
                
                cmdsMenu.revalidate();
                cmdsMenu.repaint();
            }
            
            if (menuBar != null) {
                menuBar.revalidate();
                menuBar.repaint();
            }
            
            // Обновляем кнопки
            addButton.setText(LocaleManager.t("main.add"));
            editButton.setText(LocaleManager.t("main.update"));
            deleteButton.setText(LocaleManager.t("main.delete"));
            refreshButton.setText(LocaleManager.t("main.refresh"));
            logoutButton.setText(LocaleManager.t("main.logout"));
            
            // Обновляем метки и кнопки
            filterLabel.setText(LocaleManager.t("main.filterLabel") + ":");
            applyFilterBtn.setText(LocaleManager.t("main.apply"));
            sortLabel.setText(LocaleManager.t("main.sort") + ":");
            userLabel.setText(LocaleManager.t("main.user") + ": " + currentLogin);
            
            // Обновляем заголовки таблицы
            tableModel.setColumnIdentifiers(getTranslatedColumns());
            
            // Перезаполняем комбобоксы
            filterColumnBox.removeAllItems();
            sortColumnBox.removeAllItems();
            String[] translatedCols = getTranslatedColumns();
            for (String col : translatedCols) {
                filterColumnBox.addItem(col);
                sortColumnBox.addItem(col);
            }
            
            // Восстанавливаем значения
            filterValueField.setText(currentFilterValue);
            if (currentFilterIndex >= 0) filterColumnBox.setSelectedIndex(currentFilterIndex);
            if (currentSortIndex >= 0) sortColumnBox.setSelectedIndex(currentSortIndex);
            if (reverseCheckBox != null) reverseCheckBox.setSelected(currentReverse);
            
            isUpdating = false;
            
            // Перерисовываем таблицу
            applyStreamsFilterAndSort();
            visPanel.repaint();
            
            this.revalidate();
            this.repaint();
        });
    }

    private String localizeErrorMessage(String serverMessage) {
        if (serverMessage == null) return "";
        
        // Доступ запрещён
        if (serverMessage.contains("Access denied") && serverMessage.contains("another user")) {
            return LocaleManager.t("error.accessDenied");
        }
        
        // Объект не найден
        if (serverMessage.contains("not found") || serverMessage.contains("Element not found")) {
            return LocaleManager.t("error.notFound");
        }
        
        // Требуется авторизация
        if (serverMessage.contains("Authorization is required")) {
            return LocaleManager.t("error.authRequired");
        }
        
        // Неверный логин или пароль
        if (serverMessage.contains("Wrong login or password")) {
            return LocaleManager.t("error.wrongCredentials");
        }
        
        // Нет элементов для очистки
        if (serverMessage.contains("No elements owned by you")) {
            return LocaleManager.t("error.noElementsToClear");
        }
        
        // Нет элементов с таким автором
        if (serverMessage.contains("No element found with author")) {
            return LocaleManager.t("error.noElementWithAuthor");
        }
        
        // Объект не добавлен (для add_if_min)
        if (serverMessage.contains("not less than minimum")) {
            return LocaleManager.t("error.notLessThanMinimum");
        }
        
        // Возвращаем оригинальное сообщение, если не нашли подходящего ключа
        return serverMessage;
    }
}
