package com.example.pacmanPlugin;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {

    private static final int ICON_SIZE = 16;

    private static Icon loadGif(String path) {
        // Загружаем GIF как ImageIcon
        ImageIcon originalIcon = new ImageIcon(Icons.class.getResource(path));
        // Проверяем размеры GIF
        int width = originalIcon.getIconWidth();
        int height = originalIcon.getIconHeight();

        // Если размер уже меньше или равен, оставляем как есть
        if (width <= ICON_SIZE && height <= ICON_SIZE) {
            return originalIcon;
        }

        // Масштабируем статично первый кадр GIF, чтобы размер поместился
        java.awt.Image scaledImage = originalIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, java.awt.Image.SCALE_DEFAULT);
        return new ImageIcon(scaledImage);
    }

    //public static final Icon PacMan = loadGif("/icons/PacMan.gif");
    public static final ImageIcon PacRight = new ImageIcon(Icons.class.getResource("/icons/PacManRight.gif"));
    public static final ImageIcon PacLeft = new ImageIcon(Icons.class.getResource("/icons/PacManLeft.gif"));
    public static final ImageIcon PacUp = new ImageIcon(Icons.class.getResource("/icons/PacManUp.gif"));
    public static final ImageIcon PacDown = new ImageIcon(Icons.class.getResource("/icons/PacManDown.gif"));
    public static final ImageIcon PinkGhost = new ImageIcon(Icons.class.getResource("/icons/pinkghostright.gif"));
    public static final ImageIcon BlueGhost = new ImageIcon(Icons.class.getResource("/icons/blueghostright.gif"));
}