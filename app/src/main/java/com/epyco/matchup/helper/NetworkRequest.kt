package com.epyco.matchup.helper

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.epyco.matchup.R

class NetworkRequest constructor(var context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: NetworkRequest? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkRequest(context).also {
                    INSTANCE = it
                }
            }
    }

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun handleVolleyError(error: VolleyError) {
        val message: String = when (error) {
            is NetworkError -> context.getString(R.string.errorRequestNetwork)
            is ServerError -> context.getString(R.string.errorRequestServer)
            is AuthFailureError -> context.getString(R.string.errorRequestAuth)
            is ParseError -> context.getString(R.string.errorRequestParse)
            is TimeoutError -> context.getString(R.string.errorRequestTimeout)
            else -> error.toString()
        }
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_LONG).show()
    }
}