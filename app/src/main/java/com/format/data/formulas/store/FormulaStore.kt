package com.format.data.formulas.store

import android.content.SharedPreferences
import androidx.core.content.edit
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.FormulaGroup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val LOCAL_FORMULAS_KEY = "local-formulas-key"
private const val FAVOURITE_FORMULAS_KEY = "favourite-formulas-key"
private const val USERNAME_KEY = "username-key"

class FormulaStoreImpl(
    private val sharedPreferences: SharedPreferences,
) : FormulaStore {
    override fun setKey(key: String) = sharedPreferences.edit {
        putString(USERNAME_KEY, key)
    }

    override fun resetKey() = sharedPreferences.edit {
        remove(USERNAME_KEY)
    }

    override fun getLocal(): List<FormulaGroup> = try {
        val formulaGroupsAsString = sharedPreferences.getString(key() + LOCAL_FORMULAS_KEY, emptyGroupsSerialized) ?: emptyGroupsSerialized
        Json.decodeFromString<List<FormulaGroup>>(formulaGroupsAsString)
    } catch (ex: Exception) {
        listOf()
    }

    override fun setLocal(formulaGroups: List<FormulaGroup>) = sharedPreferences.edit {
        val encodedGroups = Json.encodeToString(formulaGroups.map { it.copy(isLocal = true) })
        putString(key() + LOCAL_FORMULAS_KEY, encodedGroups)
    }

    override fun getRemoteFavourite(): List<Int> = try {
        val formulaGroupsAsString = sharedPreferences.getString(key() + FAVOURITE_FORMULAS_KEY, emptyListSerialized) ?: emptyListSerialized
        Json.decodeFromString<List<Int>>(formulaGroupsAsString)
    } catch (ex: Exception) {
        listOf()
    }

    override fun setRemoteFavourite(formulaGroups: List<Int>) = sharedPreferences.edit {
        val encodedGroups = Json.encodeToString(formulaGroups)
        putString(key() + FAVOURITE_FORMULAS_KEY, encodedGroups)
    }

    private val emptyGroupsSerialized = Json.encodeToString(listOf<FormulaGroup>())

    private val emptyListSerialized = Json.encodeToString(listOf<Int>())

    private fun key() = sharedPreferences.getString(USERNAME_KEY, "")
}