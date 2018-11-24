package com.alged.bfe.extensions

import com.alged.bfe.model.Module
import com.alged.bfe.model.ModulesSelectionConfiguration

fun List<Module>.groupModules(): List<Module> {
    val groupedModules = mutableListOf<Module>()
    val originals = this.filter { it.original }
    val groups = originals.map { original -> this.filter { it.name.contains(original.name.removeQuotes()) } }
    groups.forEach{ group ->
        group.forEach{ groupedModule ->
            groupedModules.add(groupedModule.copy(group = group))
        }
    }

    return groupedModules
}

fun List<Module>.getVisibleModules(configuration: ModulesSelectionConfiguration): List<Module> =
        when { //dual mode -> separators(node == null) and originals added
            configuration.dualModuleMode -> this.filter { it.original || it.node == null }
            else -> this
        }