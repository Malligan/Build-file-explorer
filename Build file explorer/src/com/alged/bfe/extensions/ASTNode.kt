package com.alged.bfe.extensions

import com.alged.bfe.model.ModulesSelectionConfiguration
import com.intellij.lang.ASTNode
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

fun ASTNode.inverseNodeCommenting(): ASTNode {
    return PlainTextASTFactory().createLeaf(PlainTextTokenTypes.PLAIN_TEXT, this.text.inverseStringCommenting()) as ASTNode
}