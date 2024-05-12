package com.format.data.formulas.store

import android.content.SharedPreferences
import androidx.core.content.edit
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.FormulaGroup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val LOCAL_FORMULAS_KEY = "local-formulas-key"
private const val REMOTE_FORMULAS_KEY = "remote-formulas-key"

class FormulaStoreImpl(
    private val sharedPreferences: SharedPreferences,
) : FormulaStore {
    override fun getLocal(): List<FormulaGroup> = try {
        val formulaGroupsAsString = sharedPreferences.getString(LOCAL_FORMULAS_KEY, emptyGroupsSerialized) ?: emptyGroupsSerialized
        Json.decodeFromString<List<FormulaGroup>>(formulaGroupsAsString)
    } catch (ex: Exception) {
        listOf()
    }

    override fun getRemote(): List<FormulaGroup> = try {
        val formulaGroupsAsString = sharedPreferences.getString(REMOTE_FORMULAS_KEY, emptyGroupsSerialized) ?: emptyGroupsSerialized
        Json.decodeFromString<List<FormulaGroup>>(formulaGroupsAsString)
    } catch (ex: Exception) {
        listOf()
    }

    override fun setLocal(formulaGroups: List<FormulaGroup>) = sharedPreferences.edit {
        val encodedGroups = Json.encodeToString(formulaGroups)
        putString(LOCAL_FORMULAS_KEY, encodedGroups)
    }

    override fun setRemote(formulaGroups: List<FormulaGroup>) = sharedPreferences.edit {
        val encodedGroups = Json.encodeToString(formulaGroups)
        putString(REMOTE_FORMULAS_KEY, encodedGroups)
    }

    private val emptyGroupsSerialized = Json.encodeToString(listOf<FormulaGroup>())
}