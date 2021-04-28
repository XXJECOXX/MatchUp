package com.epyco.matchup.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.service.autofill.FieldClassification
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.epyco.matchup.R
import com.epyco.matchup.models.MatchUp
import org.json.JSONException
import org.json.JSONObject


class MatchupAdapter(private val context: Context, private val listsArray: MutableList<MatchUp>) :
    RecyclerView.Adapter<MatchupAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        return MyViewHolder(inflater.inflate(R.layout.row_matchup, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val matchUp: MatchUp = listsArray[position]
        holder.valueTextView.text = matchUp.value.toString()
        holder.charactert2NameTextView.text = matchUp.characterName2
        when(matchUp.value){
            in 51..100 -> holder.valueTextView.setTextColor(Color.GREEN)
            50 -> holder.valueTextView.setTextColor(Color.parseColor("#26619C"))
            in 1..49 -> holder.valueTextView.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return listsArray.size
    }

    inner class MyViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var charactert2NameTextView: TextView = row.findViewById(R.id.character2)
        var valueTextView: TextView = row.findViewById(R.id.value)
    }
}