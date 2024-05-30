package com.format.data.infrastructure.preferences

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

private const val secretSharedPrefsFileName = "format_secret_shared_preferences_file"

class ForMatPreferences private constructor() {

    companion object {

        fun get(application: Application): SharedPreferences =
            EncryptedSharedPreferences.create(
                secretSharedPrefsFileName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                application,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }
}