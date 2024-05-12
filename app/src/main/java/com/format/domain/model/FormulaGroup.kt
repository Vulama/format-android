package com.format.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FormulaGroup(
    val name: String,
    val formulas: List<FormulaEntry>,
    val id: Int = -1,
    val isFavourite: Boolean = false,
)

@Serializable
data class FormulaEntry(
    val title: String = "",
    val formula: String = "",
    val description: String = "",
    val id: Int = -1,
)