package com.bytehamster.lib.preferencesearch

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class SearchPreference : Preference, View.OnClickListener {
    /**
     * Returns the search configuration object for this preference
     * @return The search configuration
     */
    @JvmField
    val searchConfiguration: SearchConfiguration = SearchConfiguration()
    private var hint: String? = null

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        layoutResource = R.layout.searchpreference_preference
        parseAttrs(attrs)
    }

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        layoutResource = R.layout.searchpreference_preference
        parseAttrs(attrs)
    }

    @Suppress("unused")
    constructor(context: Context) : super(context) {
        layoutResource = R.layout.searchpreference_preference
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        var a = context.obtainStyledAttributes(attrs, intArrayOf(R.attr.textHint))
        if (a.getText(0) != null) {
            hint = a.getText(0).toString()
            searchConfiguration.setTextHint(a.getText(0).toString())
        }
        a.recycle()
        a = context.obtainStyledAttributes(attrs, intArrayOf(R.attr.textClearHistory))
        if (a.getText(0) != null) {
            searchConfiguration.setTextClearHistory(a.getText(0).toString())
        }
        a.recycle()
        a = context.obtainStyledAttributes(attrs, intArrayOf(R.attr.textNoResults))
        if (a.getText(0) != null) {
            searchConfiguration.setTextNoResults(a.getText(0).toString())
        }
        a.recycle()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val searchText = holder.findViewById(R.id.search) as EditText
        searchText.isFocusable = false
        searchText.inputType = InputType.TYPE_NULL
        searchText.setOnClickListener(this)

        if (hint != null) {
            searchText.hint = hint
        }

        holder.findViewById(R.id.search_card).setOnClickListener(this)
        holder.itemView.setOnClickListener(this)
        holder.itemView.setBackgroundColor(0x0)
    }

    override fun onClick(view: View) {
        searchConfiguration.onSearch()
    }
}
