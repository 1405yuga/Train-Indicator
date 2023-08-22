package com.example.trainindicator.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.trainindicator.constants.ProjectConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

//instance of preference data store
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "railway_type_preferences")

class PreferenceRailwayType(context:Context) {

    private val RAILWAY_TYPE = stringPreferencesKey("railway_type")

    //save preferences
    suspend fun saveLayoutPrefrence(context: Context, railwayTypePreference: String) {
        context.datastore.edit { mutablePreferences ->
            mutablePreferences[RAILWAY_TYPE] = railwayTypePreference
        }
    }

    //get preference
    val preferences: Flow<String> = context.datastore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[RAILWAY_TYPE] ?: ProjectConstants.WESTERN_RAILWAY
        }
}