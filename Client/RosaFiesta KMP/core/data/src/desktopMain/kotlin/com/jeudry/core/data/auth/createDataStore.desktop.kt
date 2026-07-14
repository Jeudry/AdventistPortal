package com.adventistportal.core.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adventistportal.core.data.util.DesktopOs
import com.adventistportal.core.data.util.appDataDirectory
import com.adventistportal.core.data.util.currentOs
import java.io.File

fun createDataStore(): DataStore<Preferences> = createDataStore {
    val directory = appDataDirectory

    if(!directory.exists()) {
        directory.mkdirs()
    }

    File(directory, DATA_STORE_FILE_NAME).absolutePath
}