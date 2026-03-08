package com.example.pacmanPlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class WindowFAc implements ToolWindowFactory {

    public void createToolWindowContent(Project project,  ToolWindow toolWindow){
        PanelLogic panel=new PanelLogic();


        Content content = ContentFactory.getInstance()
                .createContent(panel, "", false);

        toolWindow.getContentManager().addContent(content);
        panel.requestFocusInWindow();
    }

}
