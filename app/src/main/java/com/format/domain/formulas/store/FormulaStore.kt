package com.format.domain.formulas.store

import com.format.domain.model.FormulaGroup

interface FormulaStore {

    fun setKey(key: String)

    fun resetKey()

    fun getLocal(): List<FormulaGroup>

    fun setLocal(formulaGroups: List<FormulaGroup>)

    fun getRemoteFavourite(): List<Int>

    fun setRemoteFavourite(formulaGroups: List<Int>)
}