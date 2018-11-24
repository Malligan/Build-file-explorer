package com.alged.bfe.model

data class ModulesSelectionConfiguration(val path: String = """('[^:]\S+')+""",
                                         val endings: List<String> = listOf("/stub"),
                                         val dualModuleMode: Boolean = endings.count() == 1)

//multi modules mode
//loop for all module variants, if at least 2 types of same name module exists add them to editor
//for example there is will be "/stub" and "/dev" modules.

//dual module mode
//add modules with selected type, single line for module in table, disabling = enabling first module variant in list of types(endings), enabling = enabling original module
//TODO KDoc