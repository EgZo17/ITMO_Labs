package com.labwork.client.ui;

import com.labwork.common.models.LabWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

public class VisualizationPanel extends JPanel {
    private List<LabWork> works = new ArrayList<>();
    private int animationTick = 0;
    private static final int POINT_RADIUS = 10;
    private static final double COORD_SCALE = 50.0;

    // Смещение для панорамирования
    private int offsetX = 0;
    private int offsetY = 0;
    private Point lastMousePoint = null;
    private boolean isDragging = false;

    // Масштабирование
    private double zoom = 1.0;
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 5.0;
    private static final double ZOOM_STEP = 0.1;

    public VisualizationPanel() {
        setBackground(new Color(30, 30, 40));
        
        // Анимация
        Timer timer = new Timer(30, e -> {
            animationTick++;
            repaint();
        });
        timer.start();

        // Обработка клика и перемещения
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePoint = e.getPoint();
                isDragging = false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDragging) {
                    checkObjectClick(e.getPoint());
                }
                lastMousePoint = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePoint != null) {
                    int dx = e.getX() - lastMousePoint.x;
                    int dy = e.getY() - lastMousePoint.y;
                    
                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                        isDragging = true;
                    }
                    
                    offsetX += dx;
                    offsetY += dy;
                    lastMousePoint = e.getPoint();
                    repaint();
                }
            }
        });

        // Обработка колесика мыши (zoom)
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldZoom = zoom;
                
                // Определяем направление прокрутки
                if (e.getWheelRotation() < 0) {
                    zoom = Math.min(zoom + ZOOM_STEP, MAX_ZOOM);
                } else {
                    zoom = Math.max(zoom - ZOOM_STEP, MIN_ZOOM);
                }
                
                // Корректируем смещение, чтобы точка под курсором оставалась на месте
                Point mousePoint = e.getPoint();
                int centerX = getWidth() / 2 + offsetX;
                int centerY = getHeight() / 2 + offsetY;
                
                // Смещение курсора относительно центра
                int dx = mousePoint.x - centerX;
                int dy = mousePoint.y - centerY;
                
                // Корректировка смещения
                offsetX -= (int)(dx * (zoom / oldZoom - 1));
                offsetY -= (int)(dy * (zoom / oldZoom - 1));
                
                repaint();
            }
        });
    }

    public void setWorks(List<LabWork> works) {
        this.works = works;
        repaint();
    }

    private void checkObjectClick(Point clickPoint) {
        for (LabWork w : works) {
            Point p = getNormalizedCoordinates(w);
            
            float mp = w.getMinimalPoint() != null ? w.getMinimalPoint() : 50f;
            float sizeMultiplier = 0.7f + (Math.max(0, Math.min(100, mp)) / 100f) * 0.8f;
            int radius = (int)(POINT_RADIUS * sizeMultiplier * zoom);
            
            // Проверяем клик с небольшим запасом (+5 пикселей)
            if (Math.hypot(clickPoint.x - p.x, clickPoint.y - p.y) < radius + 5) {
                showObjectInfo(w);
                break;
            }
        }
    }

    private Point getNormalizedCoordinates(LabWork w) {
        int panelWidth = Math.max(getWidth(), 1);
        int panelHeight = Math.max(getHeight(), 1);
        
        double rawX = w.getCoordinates().getX();
        double rawY = w.getCoordinates().getY();
        
        // Центр панели с учётом смещения
        int centerX = panelWidth / 2 + offsetX;
        int centerY = panelHeight / 2 + offsetY;
        
        // Применяем масштаб
        int pixelX = centerX + (int)(rawX * COORD_SCALE * zoom);
        int pixelY = centerY + (int)(rawY * COORD_SCALE * zoom);
        
        // Смещение для объектов с одинаковыми координатами (тоже масштабируем)
        int offsetX = (int)(((w.getId() % 25) - 12) * zoom);
        int offsetY = (int)((((w.getId() / 25) % 25) - 12) * zoom);
        
        pixelX += offsetX;
        pixelY += offsetY;
        
        return new Point(pixelX, pixelY);
    }

    private void showObjectInfo(LabWork w) {
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(w.getId()).append("\n");
        info.append("Name: ").append(w.getName()).append("\n");
        info.append("Coordinates: (").append(w.getCoordinates().getX())
            .append(", ").append(w.getCoordinates().getY()).append(")\n");
        info.append("Minimal Point: ").append(w.getMinimalPoint()).append("\n");
        if (w.getDifficulty() != null) {
            info.append("Difficulty: ").append(w.getDifficulty()).append("\n");
        }
        if (w.getAuthor() != null) {
            info.append("Author: ").append(w.getAuthor().getName()).append("\n");
            if (w.getAuthor().getHeight() > 0) {
                info.append("Height: ").append(w.getAuthor().getHeight()).append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(this,
            info.toString(),
            "Object Info",
            JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2d);

        double scale = 1.0 + 0.15 * Math.sin(animationTick * 0.2);

        for (LabWork w : works) {
            Point p = getNormalizedCoordinates(w);
            
            // Используем золотое сечение для цветов
            int ownerId = w.getOwnerId() != null ? w.getOwnerId() : 0;
            float hue = (float) ((ownerId * 0.618033988749895) % 1.0);
            Color color = Color.getHSBColor(hue, 0.85f, 0.95f);
            
            float mp = w.getMinimalPoint() != null ? w.getMinimalPoint() : 50f;
            // диапазон [0.7, 1.5]
            float sizeMultiplier = 0.7f + (Math.max(0, Math.min(100, mp)) / 100f) * 0.8f;
            int radius = (int)(POINT_RADIUS * sizeMultiplier * zoom * scale);
            
            // cвечение
            for (int i = 3; i > 0; i--) {
                int glowRadius = radius + i * 3;
                float alpha = 0.3f / i;
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
                g2d.fillOval(p.x - glowRadius, p.y - glowRadius, glowRadius * 2, glowRadius * 2);
            }
            
            // Основная точка с градиентом
            GradientPaint gradient = new GradientPaint(
                p.x - radius, p.y - radius,
                color.brighter(),
                p.x + radius, p.y + radius,
                color.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
            
            // обводка
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
            
            // Тень от текста
            g2d.setFont(new Font("Arial", Font.BOLD, Math.max(10, (int)(11 * zoom))));
            FontMetrics fm = g2d.getFontMetrics();
            String name = w.getName();
            int nameWidth = fm.stringWidth(name);
            
            // Тень текста
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(name, p.x - nameWidth/2 + 1, p.y + radius + 16);
            
            // Текст
            g2d.setColor(Color.WHITE);
            g2d.drawString(name, p.x - nameWidth/2, p.y + radius + 15);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        int panelWidth = Math.max(getWidth(), 1);
        int panelHeight = Math.max(getHeight(), 1);
        int centerX = panelWidth / 2 + offsetX;
        int centerY = panelHeight / 2 + offsetY;

        double gridSpacing = COORD_SCALE * zoom;

        g2d.setColor(new Color(50, 50, 60));
        g2d.setStroke(new BasicStroke(1));

        // Вертикальные линии
        for (int x = centerX; x < panelWidth; x += gridSpacing) {
            g2d.drawLine(x, 0, x, panelHeight);
        }
        for (int x = centerX; x >= 0; x -= gridSpacing) {
            g2d.drawLine(x, 0, x, panelHeight);
        }

        // Горизонтальные линии
        for (int y = centerY; y < panelHeight; y += gridSpacing) {
            g2d.drawLine(0, y, panelWidth, y);
        }
        for (int y = centerY; y >= 0; y -= gridSpacing) {
            g2d.drawLine(0, y, panelWidth, y);
        }

        g2d.setColor(new Color(100, 150, 200));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawLine(centerX, 0, centerX, panelHeight); // Ось Y
        g2d.drawLine(0, centerY, panelWidth, centerY); // Ось X
        
        g2d.setColor(new Color(100, 150, 200));
        int arrowSize = 8;
        // Стрелка оси X
        g2d.drawLine(panelWidth - arrowSize, centerY - arrowSize, panelWidth, centerY);
        g2d.drawLine(panelWidth - arrowSize, centerY + arrowSize, panelWidth, centerY);
        // Стрелка оси Y
        g2d.drawLine(centerX - arrowSize, arrowSize, centerX, 0);
        g2d.drawLine(centerX + arrowSize, arrowSize, centerX, 0);
    }
}
