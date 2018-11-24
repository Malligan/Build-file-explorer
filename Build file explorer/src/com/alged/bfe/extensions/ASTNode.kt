package com.alged.bfe.extensions

import com.alged.bfe.model.ModulesSelectionConfiguration
import com.intellij.lang.ASTNode
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PlainTextTokenTypes
import com.intellij.psi.impl.source.tree.PlainTextASTFactory

fun ASTNode.moduleName(configuration: ModulesSelectionConfiguration): String =
        when (val findedName = configuration.path.toRegex().find(this.text)) {
            null -> "null"
            else -> findedName.value
        }

fun ASTNode.haveOriginalName(configuration: ModulesSelectionConfiguration): Boolean =
        when (val isAnyEndingInName = configuration.endings.map { it in this.text }.find { true }) {
            null -> false
            else -> !isAnyEndingInName
        }

fun ASTNode.inverseNodeCommenting(): ASTNode =
        PlainTextASTFactory().createLeaf(PlainTextTokenTypes.PLAIN_TEXT, this.text.inverseStringCommenting()) as ASTNode

fun ASTNode.inverseNodeCommentingInParent(nodeForInverting: ASTNode, project: Project) {
    try {
        WriteCommandAction.runWriteCommandAction(project) {
            this.replaceChild(nodeForInverting, nodeForInverting.inverseNodeCommenting())
        }
    } catch (throwable: Throwable) {
        //avoid
    }
}