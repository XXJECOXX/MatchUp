package com.epyco.matchup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.epyco.matchup.R
import com.epyco.matchup.helper.Utilities
import java.util.*

class SuggestStringAdapter(context: Context?, private var list: MutableList<String>) : BaseAdapter(), Filterable {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var listFiltered = list
    override fun getCount(): Int {
        return listFiltered.size
    }

    override fun getItem(position: Int): Any {
        return listFiltered[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertedView: View?, parent: ViewGroup): View {
        val row: View
        val holder: ViewHolder
        if (convertedView == null) {
            row = inflater.inflate(R.layout.row_simple, parent, false)
            holder = ViewHolder(row)
            row.tag = holder

        } else {
            row = convertedView
            holder = row.tag as ViewHolder
        }
        val item = listFiltered[position]
        holder.suggestTextView.text = item
        return row
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(prefix: CharSequence?): FilterResults {
                val results = FilterResults()
                if (prefix.isNullOrBlank()) {
                    results.count = list.size
                    results.values = list
                } else {
                    val newValues: MutableList<String> = ArrayList()
                    val userQuery = Utilities.normalizer(prefix.toString())
                    for (value in list) {
                        if (Utilities.normalizer(value).contains(userQuery)) {
                            newValues.add(value)
                        }
                    }
                    results.count = newValues.size
                    results.values = newValues
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    listFiltered = results.values as MutableList<String>
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class ViewHolder(row: View) {
        val suggestTextView: TextView = row.findViewById(R.id.suggestTextView)
    }
}