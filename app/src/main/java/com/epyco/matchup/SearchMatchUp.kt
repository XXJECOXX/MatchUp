package com.epyco.matchup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.AutoCompleteTextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.epyco.matchup.adapters.SuggestStringAdapter
import com.epyco.matchup.helper.NetworkRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SearchMatchUp : AppCompatActivity() {
    private lateinit var networkRequest: NetworkRequest
    lateinit var gameAutoCompleteTextView: AutoCompleteTextView
    lateinit var characterAutoCompleteTextView: AutoCompleteTextView
    var gameList: MutableList<String> = mutableListOf()
    var charactersList: MutableList<String> = mutableListOf()
    lateinit var gameAdapter:SuggestStringAdapter
    lateinit var characterAdapter:SuggestStringAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_matchup)
        gameAutoCompleteTextView = findViewById(R.id.gameAutoComplete)
        characterAutoCompleteTextView = findViewById(R.id.characterAutoComplete)
        gameAdapter = SuggestStringAdapter(applicationContext,gameList)
        characterAdapter = SuggestStringAdapter(applicationContext,charactersList)
        gameAutoCompleteTextView.setAdapter(gameAdapter)
        characterAutoCompleteTextView.setAdapter(characterAdapter)

        networkRequest = NetworkRequest(applicationContext)

        networkRequest.addToRequestQueue(object : StringRequest(
            Method.POST, getString(R.string.controller, "getAllGames"),
            Response.Listener { response ->
                try {
                    val games = JSONArray(response)
                    for (i in 0 until games.length()) {
                        gameList.add(games.getString(i))
                    }
                    gameAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                }
            }, Response.ErrorListener { error -> networkRequest.handleVolleyError(error) }
        ){})

        gameAutoCompleteTextView.setOnItemClickListener { _, view, position, _ ->
            if (view != null) {
                networkRequest.addToRequestQueue(object : StringRequest(
                    Method.POST, getString(R.string.controller, "getCharactersByGame"),
                    Response.Listener { response ->
                        println(response)
                        try {
                            val characters = JSONArray(response)
                            for (i in 0 until characters.length()) {
                                charactersList.add(characters.getString(i))
                            }
                            characterAdapter.notifyDataSetChanged()
                        } catch (e: JSONException) {
                        }
                    }, Response.ErrorListener { error -> networkRequest.handleVolleyError(error) }
                ) {})
            }
        }

    }

}