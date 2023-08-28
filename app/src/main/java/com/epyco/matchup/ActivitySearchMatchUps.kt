package com.epyco.matchup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.indexOf
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.epyco.matchup.adapters.SuggestStringAdapter
import com.epyco.matchup.helper.MatchUpCache
import com.epyco.matchup.helper.NetworkRequest
import com.epyco.matchup.helper.Utilities
import org.json.JSONArray
import org.json.JSONException

class SearchMatchUpsView : AppCompatActivity() {
    private lateinit var networkRequest: NetworkRequest
    private lateinit var gameAutoCompleteTextView: AutoCompleteTextView
    private lateinit var characterAutoCompleteTextView: AutoCompleteTextView
    var gameList: MutableList<String> = mutableListOf()
    var charactersIdList: MutableList<String> = mutableListOf()
    var charactersNameList: MutableList<String> = mutableListOf()
    lateinit var cache: MatchUpCache
    lateinit var gameAdapter:SuggestStringAdapter
    lateinit var characterAdapter:SuggestStringAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_matchups)
        cache = MatchUpCache(applicationContext)
        gameAutoCompleteTextView = findViewById(R.id.gameAutoComplete)
        characterAutoCompleteTextView = findViewById(R.id.characterAutoComplete)
        gameAdapter = SuggestStringAdapter(applicationContext, gameList)
        characterAdapter = SuggestStringAdapter(applicationContext, charactersNameList)
        gameAutoCompleteTextView.setAdapter(gameAdapter)
        characterAutoCompleteTextView.setAdapter(characterAdapter)

        networkRequest = NetworkRequest(applicationContext)

        if (cache.searchViewCachedData){
            characterAutoCompleteTextView.isEnabled = true
            gameAutoCompleteTextView.setText(cache.game)
            characterAutoCompleteTextView.setText(cache.characterName)
            val games = JSONArray(cache.gameJSON)
            for (i in 0 until games.length()) {
                gameList.add(games.getString(i))
            }
            gameAdapter.notifyDataSetChanged()
            val charactersArrays = JSONArray(cache.characterJSON)
            for (i in 0 until charactersArrays.length()) {
                val character = charactersArrays.getJSONArray(i)
                charactersIdList.add(character.getString(0))
                charactersNameList.add(character.getString(1))
            }
            characterAdapter.notifyDataSetChanged()
        }
        else {
            networkRequest.addToRequestQueue(object : StringRequest(
                Method.POST, getString(R.string.controller, "getAllGames"),
                Response.Listener { response ->
                    try {
                        cache.gameJSON = response
                        val games = JSONArray(response)
                        for (i in 0 until games.length()) {
                            gameList.add(games.getString(i))
                        }
                        gameAdapter.notifyDataSetChanged()
                    } catch (_: JSONException) {
                    }
                }, Response.ErrorListener { error -> networkRequest.handleVolleyError(error) }
            ) {})
        }

        gameAutoCompleteTextView.setOnItemClickListener { adapter, view, position, _ ->
            if (view != null) {
                Utilities.hideKeyboard(this, view)
                charactersNameList.clear()
                charactersIdList.clear()
                characterAdapter.notifyDataSetChanged()
                val gameName = gameAutoCompleteTextView.text.toString()
                val gameId = gameList.indexOfFirst { it == gameName }
                cache.game = gameList[gameId]
                println("xxxxxxxxxx gameList "+gameList)
                println("xxxxxxxxxx gameId "+gameId)
                characterAutoCompleteTextView.isEnabled = true
                networkRequest.addToRequestQueue(object : StringRequest(
                    Method.POST, getString(R.string.controller, "getCharactersByGame"),
                    Response.Listener { response ->
                        println("xxxxxxxxxx response"+response)
                        try {
                            cache.characterJSON = response
                            val charactersArrays = JSONArray(response)
                            println("xxxxxxxxxx charactersArrays"+charactersArrays)
                            for (i in 0 until charactersArrays.length()) {
                                val character = charactersArrays.getJSONArray(i)
                                println("xxxxxxxxxx character "+character)
                                charactersIdList.add(character.getString(0))
                                charactersNameList.add(character.getString(1))
                            }
                            characterAdapter.notifyDataSetChanged()
                            characterAutoCompleteTextView.text.clear()
                            println("xxxxxxxxxx cache.characterId"+cache.characterId)
                            println("xxxxxxxxxx charactersIdList"+charactersIdList)
                            println("xxxxxxxxxx charactersNameList"+charactersNameList)
                        } catch (e: JSONException) {
                        }
                    }, Response.ErrorListener { error -> networkRequest.handleVolleyError(error) }
                ) {
                    override fun getParams(): MutableMap<String, String> =
                        mutableMapOf("game" to cache.game)
                })
            }
        }

        characterAutoCompleteTextView.setOnItemClickListener { _, view, position, _ ->
            if (view != null) {
                val characterName = characterAutoCompleteTextView.text.toString()
                val characterId = charactersNameList.indexOfFirst { it == characterName }
                cache.characterId = charactersIdList[characterId]
                println("xxxxxxxxxx position text "+characterAutoCompleteTextView.text.toString())
                println("xxxxxxxxxx position xx "+position)
                println("xxxxxxxxxx characterId xx "+characterId)
                println("xxxxxxxxxx cache.characterId xx "+cache.characterId)
                Utilities.hideKeyboard(this, view)
            }
        }

    }

    fun letsGo(view: View){
        Utilities.hideKeyboard(this, view)
        gameAutoCompleteTextView.error = null
        characterAutoCompleteTextView.error = null

        // Validate required fields
        if (!Utilities.required(arrayOf(gameAutoCompleteTextView, characterAutoCompleteTextView))) {
            return
        }
        var game = gameAutoCompleteTextView.text.toString().trim()
        var character = characterAutoCompleteTextView.text.toString().trim()
        //Valida si existe el personaje en la lista de personajes en la respuesta
        var position = charactersNameList.indexOf(character)
        if (position == -1){
            characterAutoCompleteTextView.text.clear()
            Utilities.required(characterAutoCompleteTextView)
            return
        }
        startActivity(Intent(applicationContext, ListMatchUpsView::class.java))
    }


    fun showInfo(view: View){
        startActivity(Intent(applicationContext, ActivityInfo::class.java))
    }

    override fun onPause() {
        super.onPause()
        characterAutoCompleteTextView.clearFocus()
        if (gameAutoCompleteTextView.text.isNotBlank() || characterAutoCompleteTextView.text.isNotBlank()){
            cache.searchViewCachedData = true
            cache.game = gameAutoCompleteTextView.text.toString().trim()
            cache.characterName = characterAutoCompleteTextView.text.toString().trim()
        }
        else {
            cache.searchViewCachedData = false
        }
    }

}