package com.epyco.matchup.helper

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class MatchUpCache(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences("MatchupCache",Context.MODE_PRIVATE)

    var game: String
        get() = preferences.getString("game", null) ?: ""
        set(value) {
            preferences.edit().putString("game", value).apply()
        }
    var characterId: String
        get() = preferences.getString("characterId", null) ?: ""
        set(value) {
            preferences.edit().putString("characterId", value).apply()
        }
    var characterName: String
        get() = preferences.getString("characterName", null) ?: ""
        set(value) {
            preferences.edit().putString("characterName", value).apply()
        }
    var characterJSON: String
        get() = preferences.getString("characterJSON", null) ?: ""
        set(value) {
            preferences.edit().putString("characterJSON", value).apply()
        }
}