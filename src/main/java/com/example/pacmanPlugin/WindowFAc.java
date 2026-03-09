package com.example.pacmanPlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javax.swing.*;
import java.awt.*;

public class WindowFAc implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JPanel mainWrapper = new JPanel(new BorderLayout());

        PanelLogic gamePanel = new PanelLogic();

        JButton restartButton = new JButton("Restart Game");

        restartButton.setFocusable(false);

        restartButton.addActionListener(e -> {
            gamePanel.restartGame();
            gamePanel.requestFocusInWindow();
        });

        mainWrapper.add(restartButton, BorderLayout.NORTH);
        mainWrapper.add(gamePanel, BorderLayout.CENTER);

        Content content = ContentFactory.getInstance()
                .createContent(mainWrapper, "", false);

        toolWindow.getContentManager().addContent(content);

        gamePanel.requestFocusInWindow();
    }
}