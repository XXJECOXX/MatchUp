package com.epyco.matchup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.epyco.matchup.helper.MatchUpCache
import com.epyco.matchup.helper.NetworkRequest
import com.epyco.matchup.models.MatchUp
import org.json.JSONArray
import org.json.JSONException

class ListMatchUpsView : AppCompatActivity() {
    var matchUpsList: MutableList<MatchUp> = mutableListOf()
    val cache = MatchUpCache(applicationContext)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_matchup)
        val networkRequest = NetworkRequest(applicationContext)
        networkRequest.addToRequestQueue(object : StringRequest(
            Method.POST, getString(R.string.controller, "getCharacterMatchUps"),
            Response.Listener { response ->
                println(response)
                try {
                    val charactersCache = JSONArray(cache.characterJSON)
                    val matchUpsArrays = JSONArray(response)
                    for (i in 0 until matchUpsArrays.length()) {
                        val matchUpArray = matchUpsArrays.getJSONArray(i)
                        var characterId1 = matchUpArray[0]
                        var characterId2 = matchUpArray[1]
                        var matchupValue = matchUpArray[2]
                    }
                } catch (e: JSONException) {
                }
            }, Response.ErrorListener { error -> networkRequest.handleVolleyError(error) }
        ) {
            override fun getParams(): MutableMap<String, String> = mutableMapOf("characterId" to cache.characterId)
        })

    }

    fun orderByCharacter(view: View) {}
    fun orderByMatchup(view: View) {}
}