package com.epyco.matchup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.epyco.matchup.adapters.MatchupAdapter
import com.epyco.matchup.helper.MatchUpCache
import com.epyco.matchup.helper.NetworkRequest
import com.epyco.matchup.models.MatchUp
import org.json.JSONArray
import org.json.JSONException

class ListMatchUpsView : AppCompatActivity() {
    var matchUpsList: MutableList<MatchUp> = mutableListOf()
    lateinit var matchUpRecycler: RecyclerView
    lateinit var matchUpAdapter: MatchupAdapter
    lateinit var cache: MatchUpCache
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_matchup)
        cache = MatchUpCache(applicationContext)
        val networkRequest = NetworkRequest(applicationContext)
        matchUpRecycler = findViewById(R.id.list_matchUps)
        matchUpAdapter = MatchupAdapter(applicationContext,matchUpsList)
        matchUpRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        matchUpRecycler.adapter = matchUpAdapter
        networkRequest.addToRequestQueue(object : StringRequest(
            Method.POST, getString(R.string.controller, "getCharacterMatchUps"),
            Response.Listener { response ->
                println(response)
                try {
                    val charactersCache = JSONArray(cache.characterJSON)
                    val matchUpsArrays = JSONArray(response)
                    for (i in 0 until matchUpsArrays.length()) {
                        val matchUpArray = matchUpsArrays.getJSONArray(i)
                        var characterId2 = matchUpArray[1]
                        var matchupValue = matchUpArray.getInt(2)
                        var characterName2 = ""
                        for (i in 0 until charactersCache.length()){
                            if (charactersCache.getJSONArray(i)[0] == characterId2){
                                characterName2 = charactersCache.getJSONArray(i)[1].toString()
                                break
                            }
                        }
                        matchUpsList.add(MatchUp(cache.characterName,characterName2,matchupValue))
                    }
                    matchUpAdapter.notifyDataSetChanged()
                    println(matchUpsList)
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