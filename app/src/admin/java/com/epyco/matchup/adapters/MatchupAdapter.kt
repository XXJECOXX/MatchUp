package com.epyco.matchup.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epyco.matchup.R
import com.epyco.matchup.models.MatchUp


class MatchupAdapter(private val context: Context, private val listsArray: MutableList<MatchUp>) :
    RecyclerView.Adapter<MatchupAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        return MyViewHolder(inflater.inflate(R.layout.row_matchup, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val matchUp: MatchUp = listsArray[position]
        holder.valueEditText.setText(matchUp.value.toString())
        holder.charactert2NameTextView.text = matchUp.characterName2
        when(matchUp.value){
            in 51..100 -> holder.valueEditText.setTextColor(Color.GREEN)
            50 -> holder.valueEditText.setTextColor(Color.parseColor("#26619C"))
            in 1..49 -> holder.valueEditText.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return listsArray.size
    }

    inner class MyViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var charactert2NameTextView: TextView = row.findViewById(R.id.character2)
        var valueEditText: EditText = row.findViewById(R.id.value)
    }
}