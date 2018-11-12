package com.alged.bfe

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory



class ExplorerToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val explorerToolWindow = ExplorerToolWindow(toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(explorerToolWindow.getContent(), "", false)
        toolWindow.contentManager.addContent(content)

        explorerToolWindow.setCurrentProject(project)
    }
}