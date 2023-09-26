package com.epyco.matchup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.epyco.matchup.adapters.SuggestStringAdapter
import com.epyco.matchup.helper.MatchUpCache
import com.epyco.matchup.helper.NetworkRequest
import com.epyco.matchup.helper.Utilities
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import org.json.JSONArray
import org.json.JSONException

class ActivitySearchMatchUps : AppCompatActivity(), OnUserEarnedRewardListener {
    lateinit var mAdView: AdView
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private final var TAG = "ActivitySearchMatchUps"
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
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        MobileAds.initialize(this) { initializationStatus ->
            loadAd()
        }
        val adRequest: AdRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        cache = MatchUpCache(applicationContext)
        gameAutoCompleteTextView = findViewById(R.id.gameAutoComplete)
        characterAutoCompleteTextView = findViewById(R.id.characterAutoComplete)
        gameAdapter = SuggestStringAdapter(applicationContext, gameList)
        characterAdapter = SuggestStringAdapter(applicationContext, charactersNameList)
        gameAutoCompleteTextView.setAdapter(gameAdapter)
        characterAutoCompleteTextView.setAdapter(characterAdapter)

        networkRequest = NetworkRequest(applicationContext)
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

        gameAutoCompleteTextView.setOnItemClickListener { adapter, view, position, _ ->
            if (view != null) {
                Utilities.hideKeyboard(this, view)
                charactersNameList.clear()
                charactersIdList.clear()
                characterAdapter.notifyDataSetChanged()
                val gameName = gameAutoCompleteTextView.text.toString()
                val gameId = gameList.indexOfFirst { it == gameName }
                cache.game = gameList[gameId]
                characterAutoCompleteTextView.isEnabled = true
                networkRequest.addToRequestQueue(object : StringRequest(
                    Method.POST, getString(R.string.controller, "getCharactersByGame"),
                    Response.Listener { response ->
                        try {
                            cache.characterJSON = response
                            val charactersArrays = JSONArray(response)
                            for (i in 0 until charactersArrays.length()) {
                                val character = charactersArrays.getJSONArray(i)
                                charactersIdList.add(character.getString(0))
                                charactersNameList.add(character.getString(1))
                            }
                            characterAdapter.notifyDataSetChanged()
                            characterAutoCompleteTextView.text.clear()
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
        rewardedInterstitialAd?.show(this, this)
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

    private fun loadAd() {
        RewardedInterstitialAd.load(this, "ca-app-pub-9983989988655125/9327784625",
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    rewardedInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            startActivity(Intent(applicationContext, ActivityCharacterMatchUpsList::class.java))
                            Log.d(TAG, "Ad dismissed fullscreen content.")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            startActivity(Intent(applicationContext, ActivityCharacterMatchUpsList::class.java))
                            Log.e(TAG, "Ad failed to show fullscreen content.")
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(TAG, "Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.toString()?.let { Log.d(TAG, it) }
                }
            })
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        val toast = Toast.makeText(this, "Recompensa obtenida, Gracias", Toast.LENGTH_LONG)
        toast.show()
    }
    override fun onBackPressed() {
        finish()
    }

}