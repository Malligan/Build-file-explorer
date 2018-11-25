package com.alged.bfe.extensions

import com.alged.bfe.model.ModulesSelectionConfiguration
import com.alged.bfe.model.Module
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFile

fun PsiFile.getModules(configuration: ModulesSelectionConfiguration): List<Module> {
    val modulesWithSeparator = mutableListOf<Module>()

    val projectNodes: MutableList<ASTNode> = this.node.getChildren(null).filter { it.text.contains("project") }.toMutableList()
    val allModules = projectNodes.map { Module.fromASTNode(it, configuration, this) }
    val correctNamedModules = allModules.filter { it.name != "null" }
    val correctGroupedModules = correctNamedModules.groupModules().filter { it.group.count() > 1 }

    if (!correctGroupedModules.isEmpty()) {
        modulesWithSeparator.add(Module.separator())
        modulesWithSeparator.addAll(correctGroupedModules)

        return modulesWithSeparator
    }

    return emptyList()
}