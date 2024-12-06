package com.bytehamster.lib.preferencesearch

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytehamster.lib.preferencesearch.SearchConfiguration.Companion.fromBundle
import com.bytehamster.lib.preferencesearch.SearchPreferenceAdapter.SearchClickListener
import com.bytehamster.lib.preferencesearch.ui.AnimationUtils

class SearchPreferenceFragment : Fragment(), SearchClickListener {
    private lateinit var searcher: PreferenceParser
    private var results: List<PreferenceItem>? = null
    private var history: MutableList<HistoryItem>? = null
    private var prefs: SharedPreferences? = null
    private lateinit var viewHolder: SearchViewHolder
    private lateinit var searchConfiguration: SearchConfiguration
    private var adapter: SearchPreferenceAdapter? = null
    private var historyClickListener: ((String) -> Unit)? = null
    private var searchTermPreset: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = requireContext().getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        searcher = PreferenceParser(requireContext())

        searchConfiguration = fromBundle(requireArguments())
        searchConfiguration.files?.forEach { file ->
            searcher.addResourceFile(file)
        }
        searcher.addPreferenceItems(searchConfiguration.preferencesToIndex)
        loadHistory()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.searchpreference_fragment, container, false)
        viewHolder = SearchViewHolder(rootView)

        viewHolder.clearButton.setOnClickListener { _: View? ->
            viewHolder.searchView.setText(
                "",
            )
        }
        if (searchConfiguration.isHistoryEnabled()) {
            viewHolder.moreButton.visibility = View.VISIBLE
        }
        if (searchConfiguration.getTextHint() != null) {
            viewHolder.searchView.hint = searchConfiguration.getTextHint()
        }
        if (searchConfiguration.getTextNoResults() != null) {
            viewHolder.noResults.text = searchConfiguration.getTextNoResults()
        }
        viewHolder.moreButton.setOnClickListener { _: View? ->
            val popup =
                PopupMenu(
                    requireContext(),
                    viewHolder.moreButton,
                )
            popup.menuInflater.inflate(R.menu.searchpreference_more, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.clear_history) {
                    clearHistory()
                }
                true
            }
            if (searchConfiguration.getTextClearHistory() != null) {
                popup.menu.findItem(R.id.clear_history)
                    .setTitle(searchConfiguration.getTextClearHistory())
            }
            popup.show()
        }

        viewHolder.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
            )
        adapter = SearchPreferenceAdapter()
        adapter!!.setSearchConfiguration(searchConfiguration)
        adapter!!.setOnItemClickListener(this)
        viewHolder.recyclerView.adapter = adapter

        viewHolder.searchView.addTextChangedListener(textWatcher)

        if (!searchConfiguration.isSearchBarEnabled()) {
            viewHolder.cardView.visibility = View.GONE
        }

        if (searchTermPreset != null) {
            viewHolder.searchView.setText(searchTermPreset)
        }

        val anim = searchConfiguration.revealAnimationSetting
        if (anim != null) {
            AnimationUtils.registerCircularRevealAnimation(
                requireContext(),
                rootView,
                anim,
            )
        }
        rootView.setOnTouchListener { _: View?, _: MotionEvent? -> true }
        return rootView
    }

    private fun loadHistory() {
        history = mutableListOf()
        if (!searchConfiguration.isHistoryEnabled()) {
            return
        }

        val size = prefs!!.getInt(historySizeKey(), 0)
        for (i in 0 until size) {
            val title = prefs!!.getString(historyEntryKey(i), null)
            history?.add(HistoryItem(title!!))
        }
    }

    private fun saveHistory() {
        val editor = prefs!!.edit()
        editor.putInt(historySizeKey(), history!!.size)
        for (i in history!!.indices) {
            editor.putString(historyEntryKey(i), history!![i].term)
        }
        editor.apply()
    }

    /**
     * Gets the preference key for the history size, prefixed with the history ID, if set.
     * @return the preference key for the history size
     */
    private fun historySizeKey(): String {
        return if (searchConfiguration.getHistoryId() != null) {
            searchConfiguration.getHistoryId() + "_history_size"
        } else {
            "history_size"
        }
    }

    /**
     * Gets the preference key for a history entry, prefixed with the history ID, if set.
     * @return the preference key for the history entry
     */
    private fun historyEntryKey(i: Int): String {
        return if (searchConfiguration.getHistoryId() != null) {
            searchConfiguration.getHistoryId() + "_history_" + i
        } else {
            "history_$i"
        }
    }

    private fun clearHistory() {
        viewHolder.searchView.setText("")
        history!!.clear()
        saveHistory()
        updateSearchResults("")
    }

    private fun addHistoryEntry(entry: String) {
        val newItem = HistoryItem(entry)
        if (!history!!.contains(newItem)) {
            if (history!!.size >= MAX_HISTORY) {
                history!!.removeAt(history!!.size - 1)
            }
            history!!.add(0, newItem)
            saveHistory()
            updateSearchResults(viewHolder.searchView.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        updateSearchResults(viewHolder.searchView.text.toString())

        if (searchConfiguration.isSearchBarEnabled()) {
            showKeyboard()
        }
    }

    private fun showKeyboard() {
        val searchView = viewHolder?.searchView ?: return
        searchView.post {
            searchView.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(
                searchView,
                InputMethodManager.SHOW_IMPLICIT,
            )
        }
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus ?: return
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @Suppress("unused")
    fun setSearchTerm(term: CharSequence?) {
        if (this::viewHolder.isInitialized) {
            viewHolder.searchView.setText(term)
        } else {
            searchTermPreset = term
        }
    }

    private fun updateSearchResults(keyword: String) {
        if (TextUtils.isEmpty(keyword)) {
            showHistory()
            return
        }

        results = searcher.searchFor(keyword, searchConfiguration.isFuzzySearchEnabled())
        adapter!!.setContent(results!!.toList())

        setEmptyViewShown(results!!.isEmpty())
    }

    private fun setEmptyViewShown(shown: Boolean) {
        if (shown) {
            viewHolder.noResults.visibility = View.VISIBLE
            viewHolder.recyclerView.visibility = View.GONE
        } else {
            viewHolder.noResults.visibility = View.GONE
            viewHolder.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showHistory() {
        viewHolder.noResults.visibility = View.GONE
        viewHolder.recyclerView.visibility = View.VISIBLE

        adapter!!.setContent(history!!.toList())
        setEmptyViewShown(history!!.isEmpty())
    }

    override fun onItemClicked(
        item: ListItem?,
        position: Int,
    ) {
        if (item?.type == HistoryItem.TYPE) {
            val text: CharSequence = (item as HistoryItem).term
            viewHolder.searchView.setText(text)
            viewHolder.searchView.setSelection(text.length)
            historyClickListener?.invoke(text.toString())
        } else {
            if (activity !is SearchPreferenceResultListener && parentFragment !is SearchPreferenceResultListener) {
                throw IllegalStateException("Parent fragment or Activity must implement SearchPreferenceResultListener")
            }
            hideKeyboard()
            val r = results!![position]
            addHistoryEntry(r.title!!)
            val result = SearchPreferenceResult(r.key!!, r.resId)
            (activity as? SearchPreferenceResultListener)?.onSearchResultClicked(result)
            (parentFragment as? SearchPreferenceResultListener)?.onSearchResultClicked(result)
        }
    }

    private val textWatcher: TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int,
            ) {}

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int,
            ) {}

            override fun afterTextChanged(editable: Editable) {
                updateSearchResults(editable.toString())
                viewHolder.clearButton.isVisible = editable.toString().isNotEmpty()
            }
        }

    @Suppress("unused")
    fun setHistoryClickListener(historyClickListener: (String) -> Unit) {
        this.historyClickListener = historyClickListener
    }

    private class SearchViewHolder(root: View) {
        val clearButton: ImageView = root.findViewById(R.id.clear)
        val moreButton: ImageView = root.findViewById(R.id.more)
        val searchView: EditText = root.findViewById(R.id.search)
        val recyclerView: RecyclerView = root.findViewById(R.id.list)
        val noResults: TextView = root.findViewById(R.id.no_results)
        val cardView: CardView = root.findViewById(R.id.search_card)
    }

    companion object {
        private const val SHARED_PREFS_FILE = "preferenceSearch"
        private const val MAX_HISTORY = 5
    }
}
