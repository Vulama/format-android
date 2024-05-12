package com.format.domain.formulas.store

import com.format.domain.model.FormulaGroup

interface FormulaStore {

    fun getLocal(): List<FormulaGroup>

    fun getRemote(): List<FormulaGroup>

    fun setLocal(formulaGroups: List<FormulaGroup>)

    fun setRemote(formulaGroups: List<FormulaGroup>)
}