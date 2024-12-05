package com.bytehamster.lib.preferencesearch

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.ColorInt
import androidx.annotation.XmlRes
import androidx.core.os.BundleCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.bytehamster.lib.preferencesearch.Breadcrumb.concat
import com.bytehamster.lib.preferencesearch.ui.RevealAnimationSetting

class SearchConfiguration {
    var files: ArrayList<SearchIndexItem>? = ArrayList()
        private set
    var preferencesToIndex: ArrayList<PreferenceItem>? = ArrayList()
        private set
    val bannedKeys: ArrayList<String> = ArrayList()
    private var historyEnabled = true
    private var historyId: String? = null
    private var breadcrumbsEnabled = false
    private var fuzzySearchEnabled = true
    private var searchBarEnabled = true
    var revealAnimationSetting: RevealAnimationSetting? = null
        private set
    private var textClearHistory: String? = null
    private var textNoResults: String? = null
    private var textHint: String? = null

    private var onSearchListener: ((SearchPreferenceFragment) -> Unit)? = null

    fun setOnSearchListener(onSearchListener: (SearchPreferenceFragment) -> Unit): SearchConfiguration {
        this.onSearchListener = onSearchListener
        return this
    }

    private var onSearchResultClickedListener: (SearchPreferenceResultListener)? = null

    fun setOnSearchResultClickedListener(onSearchResultClickedListener: SearchPreferenceResultListener): SearchConfiguration {
        this.onSearchResultClickedListener = onSearchResultClickedListener
        return this
    }

    fun onSearch() {
        val fragment =
            SearchPreferenceFragment().apply {
                arguments = toBundle()
                onSearchResultClickedListener?.let { this.setOnSearchResultClickedListener(it) }
            }
        onSearchListener?.invoke(fragment)
    }

    private fun toBundle(): Bundle {
        val arguments = Bundle()
        arguments.putParcelableArrayList(
            ARGUMENT_INDEX_FILES,
            files,
        )
        arguments.putParcelableArrayList(ARGUMENT_INDEX_INDIVIDUAL_PREFERENCES, preferencesToIndex)
        arguments.putBoolean(ARGUMENT_HISTORY_ENABLED, historyEnabled)
        arguments.putParcelable(ARGUMENT_REVEAL_ANIMATION_SETTING, revealAnimationSetting)
        arguments.putBoolean(ARGUMENT_FUZZY_ENABLED, fuzzySearchEnabled)
        arguments.putBoolean(ARGUMENT_BREADCRUMBS_ENABLED, breadcrumbsEnabled)
        arguments.putBoolean(ARGUMENT_SEARCH_BAR_ENABLED, searchBarEnabled)
        arguments.putString(ARGUMENT_TEXT_HINT, textHint)
        arguments.putString(ARGUMENT_TEXT_CLEAR_HISTORY, textClearHistory)
        arguments.putString(ARGUMENT_TEXT_NO_RESULTS, textNoResults)
        arguments.putString(ARGUMENT_HISTORY_ID, historyId)
        return arguments
    }

    /**
     * Show a history of recent search terms if nothing was typed yet. Default is true
     * @param historyEnabled True if history should be enabled
     */
    fun setHistoryEnabled(historyEnabled: Boolean) {
        this.historyEnabled = historyEnabled
    }

    /**
     * Sets the id to use for saving the history. Preference screens with the same history id will share the same
     * history. The default id is null (no id).
     * @param historyId the history id
     */
    fun setHistoryId(historyId: String?) {
        this.historyId = historyId
    }

    /**
     * Allow to enable and disable fuzzy searching. Default is true
     * @param fuzzySearchEnabled True if search should be fuzzy
     */
    fun setFuzzySearchEnabled(fuzzySearchEnabled: Boolean) {
        this.fuzzySearchEnabled = fuzzySearchEnabled
    }

    /**
     * Show breadcrumbs in the list of search results, containing of
     * the prefix given in addResourceFileToIndex, PreferenceCategory and PreferenceScreen.
     * Default is false
     * @param breadcrumbsEnabled True if breadcrumbs should be shown
     */
    fun setBreadcrumbsEnabled(breadcrumbsEnabled: Boolean) {
        this.breadcrumbsEnabled = breadcrumbsEnabled
    }

    /**
     * Show the search bar above the list. When setting this to false, you have to use {@see SearchPreferenceFragment#setSearchTerm(String) setSearchTerm} instead
     * Default is true
     * @param searchBarEnabled True if search bar should be shown
     */
    fun setSearchBarEnabled(searchBarEnabled: Boolean) {
        this.searchBarEnabled = searchBarEnabled
    }

    /**
     * Display a reveal animation
     * @param centerX Origin of the reveal animation
     * @param centerY Origin of the reveal animation
     * @param width Size of the main container
     * @param height Size of the main container
     * @param colorAccent Accent color to use
     */
    fun useAnimation(
        centerX: Int,
        centerY: Int,
        width: Int,
        height: Int,
        @ColorInt colorAccent: Int,
    ) {
        revealAnimationSetting =
            RevealAnimationSetting(centerX, centerY, width, height, colorAccent)
    }

    /**
     * Adds a new file to the index
     * @param resId The preference file to index
     */
    fun index(
        @XmlRes resId: Int,
    ): SearchIndexItem {
        val item = SearchIndexItem(resId, this)
        files!!.add(item)
        return item
    }

    /**
     * Indexes a single preference
     * @return the indexed PreferenceItem to configure it with chaining
     * @see PreferenceItem for the available methods for configuring it
     */
    @Suppress("unused")
    fun indexItem(): PreferenceItem {
        val preferenceItem = PreferenceItem()
        preferencesToIndex!!.add(preferenceItem)
        return preferenceItem
    }

    /**
     * Indexes a single android preference
     * @param preference to get its key, summary, title and entries
     * @return the indexed PreferenceItem to configure it with chaining
     * @see PreferenceItem for the available methods for configuring it
     */
    @Suppress("unused")
    fun indexItem(preference: Preference): PreferenceItem {
        val preferenceItem = PreferenceItem()

        if (preference.key != null) {
            preferenceItem.key = preference.key
        }
        if (preference.summary != null) {
            preferenceItem.summary = preference.summary.toString()
        }
        if (preference.title != null) {
            preferenceItem.title = preference.title.toString()
        }
        if (preference is ListPreference) {
            if (preference.entries != null) {
                preferenceItem.entries = preference.entries.contentToString()
            }
        }
        preferencesToIndex!!.add(preferenceItem)
        return preferenceItem
    }

    /**
     * @param key of the preference to be ignored
     */
    fun ignorePreference(key: String) {
        bannedKeys.add(key)
    }

    fun isHistoryEnabled(): Boolean {
        return historyEnabled
    }

    fun getHistoryId(): String? {
        return historyId
    }

    fun isBreadcrumbsEnabled(): Boolean {
        return breadcrumbsEnabled
    }

    fun isFuzzySearchEnabled(): Boolean {
        return fuzzySearchEnabled
    }

    fun isSearchBarEnabled(): Boolean {
        return searchBarEnabled
    }

    fun getTextClearHistory(): String? {
        return textClearHistory
    }

    fun setTextClearHistory(textClearHistory: String?) {
        this.textClearHistory = textClearHistory
    }

    fun getTextNoResults(): String? {
        return textNoResults
    }

    fun setTextNoResults(textNoResults: String?) {
        this.textNoResults = textNoResults
    }

    fun getTextHint(): String? {
        return textHint
    }

    fun setTextHint(textHint: String?) {
        this.textHint = textHint
    }

    /**
     * Adds a given R.xml resource to the search index
     */
    class SearchIndexItem : Parcelable {
        var breadcrumb: String? = ""
            private set

        @get:XmlRes
        @XmlRes
        val resId: Int
        val searchConfiguration: SearchConfiguration?

        /**
         * Includes the given R.xml resource in the index
         * @param resId The resource to index
         */
        constructor(
            @XmlRes resId: Int,
            searchConfiguration: SearchConfiguration,
        ) {
            this.resId = resId
            this.searchConfiguration = searchConfiguration
        }

        /**
         * Adds a breadcrumb
         * @param breadcrumb The breadcrumb to add
         * @return For chaining
         */
        fun addBreadcrumb(breadcrumb: String): SearchIndexItem {
            assertNotParcel()
            this.breadcrumb = concat(this.breadcrumb, breadcrumb)
            return this
        }

        /**
         * Throws an exception if the item does not have a searchConfiguration (thus, is restored from a parcel)
         */
        private fun assertNotParcel() {
            checkNotNull(searchConfiguration) { "SearchIndexItems that are restored from parcel can not be modified." }
        }

        private constructor(parcel: Parcel) {
            this.breadcrumb = parcel.readString()
            this.resId = parcel.readInt()
            this.searchConfiguration = null
        }

        override fun writeToParcel(
            dest: Parcel,
            flags: Int,
        ) {
            dest.writeString(this.breadcrumb)
            dest.writeInt(this.resId)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {
            @JvmField
            val CREATOR: Creator<SearchIndexItem> =
                object : Creator<SearchIndexItem> {
                    override fun createFromParcel(`in`: Parcel): SearchIndexItem {
                        return SearchIndexItem(`in`)
                    }

                    override fun newArray(size: Int): Array<SearchIndexItem?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    companion object {
        private const val ARGUMENT_INDEX_FILES = "items"
        private const val ARGUMENT_INDEX_INDIVIDUAL_PREFERENCES = "individual_prefs"
        private const val ARGUMENT_FUZZY_ENABLED = "fuzzy"
        private const val ARGUMENT_HISTORY_ENABLED = "history_enabled"
        private const val ARGUMENT_HISTORY_ID = "history_id"
        private const val ARGUMENT_SEARCH_BAR_ENABLED = "search_bar_enabled"
        private const val ARGUMENT_BREADCRUMBS_ENABLED = "breadcrumbs_enabled"
        private const val ARGUMENT_REVEAL_ANIMATION_SETTING = "reveal_anim_setting"
        private const val ARGUMENT_TEXT_HINT = "text_hint"
        private const val ARGUMENT_TEXT_CLEAR_HISTORY = "text_clear_history"
        private const val ARGUMENT_TEXT_NO_RESULTS = "text_no_results"

        @JvmStatic
        fun fromBundle(bundle: Bundle): SearchConfiguration {
            val config = SearchConfiguration()
            config.files = BundleCompat.getParcelableArrayList(bundle, ARGUMENT_INDEX_FILES, SearchIndexItem::class.java)
            config.preferencesToIndex =
                BundleCompat.getParcelableArrayList(
                    bundle, ARGUMENT_INDEX_INDIVIDUAL_PREFERENCES, PreferenceItem::class.java,
                )
            config.historyEnabled = bundle.getBoolean(ARGUMENT_HISTORY_ENABLED)
            config.revealAnimationSetting =
                BundleCompat.getParcelable(
                    bundle, ARGUMENT_REVEAL_ANIMATION_SETTING, RevealAnimationSetting::class.java,
                )
            config.fuzzySearchEnabled = bundle.getBoolean(ARGUMENT_FUZZY_ENABLED)
            config.breadcrumbsEnabled = bundle.getBoolean(ARGUMENT_BREADCRUMBS_ENABLED)
            config.searchBarEnabled = bundle.getBoolean(ARGUMENT_SEARCH_BAR_ENABLED)
            config.textHint = bundle.getString(ARGUMENT_TEXT_HINT)
            config.textClearHistory = bundle.getString(ARGUMENT_TEXT_CLEAR_HISTORY)
            config.textNoResults = bundle.getString(ARGUMENT_TEXT_NO_RESULTS)
            config.historyId = bundle.getString(ARGUMENT_HISTORY_ID)
            return config
        }
    }
}
