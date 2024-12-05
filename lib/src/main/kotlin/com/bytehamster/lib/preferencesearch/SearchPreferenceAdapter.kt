package com.bytehamster.lib.preferencesearch

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class SearchPreferenceAdapter :
    RecyclerView.Adapter<SearchPreferenceAdapter.ViewHolder>() {
    private var dataset: List<ListItem>
    private var searchConfiguration: SearchConfiguration? = null
    private var onItemClickListener: SearchClickListener? = null

    init {
        dataset = ArrayList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return if (viewType == PreferenceItem.TYPE) {
            PreferenceViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.searchpreference_list_item_result,
                    parent,
                    false,
                ),
            )
        } else {
            HistoryViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.searchpreference_list_item_history,
                    parent,
                    false,
                ),
            )
        }
    }

    override fun onBindViewHolder(
        h: ViewHolder,
        position: Int,
    ) {
        val listItem = dataset[position]
        if (getItemViewType(position) == HistoryItem.TYPE) {
            val holder = h as HistoryViewHolder
            val item = listItem as HistoryItem
            holder.term.text = item.term
        } else if (getItemViewType(position) == PreferenceItem.TYPE) {
            val holder = h as PreferenceViewHolder
            val item = listItem as PreferenceItem
            holder.title.text = item.title

            if (TextUtils.isEmpty(item.summary)) {
                holder.summary.visibility = View.GONE
            } else {
                holder.summary.visibility = View.VISIBLE
                holder.summary.text = item.summary
            }

            if (searchConfiguration!!.isBreadcrumbsEnabled()) {
                holder.breadcrumbs.text = item.breadcrumbs
                holder.breadcrumbs.alpha = 0.6f
                holder.summary.alpha = 1.0f
            } else {
                holder.breadcrumbs.visibility = View.GONE
                holder.summary.alpha = 0.6f
            }
        }

        h.root.setOnClickListener { v: View? ->
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClicked(listItem, h.absoluteAdapterPosition)
            }
        }
    }

    fun setContent(items: List<ListItem>) {
        dataset = ArrayList(items)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataset[position].type
    }

    fun setSearchConfiguration(searchConfiguration: SearchConfiguration?) {
        this.searchConfiguration = searchConfiguration
    }

    fun setOnItemClickListener(onItemClickListener: SearchClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    internal interface SearchClickListener {
        fun onItemClicked(
            item: ListItem?,
            position: Int,
        )
    }

    internal open class ViewHolder(var root: View) : RecyclerView.ViewHolder(
        root,
    )

    internal class HistoryViewHolder(v: View) : ViewHolder(v) {
        var term: TextView = v.findViewById(R.id.term)
    }

    internal class PreferenceViewHolder(v: View) : ViewHolder(v) {
        var title: TextView = v.findViewById(R.id.title)
        var summary: TextView = v.findViewById(R.id.summary)
        var breadcrumbs: TextView = v.findViewById(R.id.breadcrumbs)
    }
}
