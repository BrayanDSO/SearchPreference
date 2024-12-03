package com.bytehamster.preferencesearch

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bytehamster.lib.preferencesearch.SearchPreference
import com.bytehamster.lib.preferencesearch.SearchPreferenceActionView
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener


/**
 * This file demonstrates how to use the library without actually displaying a PreferenceFragment
 */
class SearchViewExample : AppCompatActivity(), SearchPreferenceResultListener {
    private var searchPreferenceActionView: SearchPreferenceActionView? = null
    private var searchPreferenceMenuItem: MenuItem? = null
    private var savedInstanceSearchQuery: String? = null
    private var savedInstanceSearchEnabled = false
    private var prefsFragment: PrefsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            savedInstanceSearchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY)
            savedInstanceSearchEnabled = savedInstanceState.getBoolean(KEY_SEARCH_ENABLED)
        }

        prefsFragment = PrefsFragment()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, prefsFragment!!).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        searchPreferenceMenuItem = menu.findItem(R.id.search)
        searchPreferenceActionView =
            searchPreferenceMenuItem?.getActionView() as SearchPreferenceActionView?
        val searchConfiguration = searchPreferenceActionView!!.searchConfiguration
        searchConfiguration.index(R.xml.preferences)

        searchConfiguration.useAnimation(
            findViewById<View>(android.R.id.content).width - supportActionBar!!.height / 2,
            -supportActionBar!!.height / 2,
            findViewById<View>(android.R.id.content).width,
            findViewById<View>(android.R.id.content).height,
            resources.getColor(R.color.colorPrimary)
        )

        searchPreferenceActionView!!.activity = this

        val searchPreferenceMenuItem = menu.findItem(R.id.search)
        searchPreferenceMenuItem.setOnActionExpandListener(object :
            MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchPreferenceActionView!!.cancelSearch()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }
        })

        if (savedInstanceSearchEnabled) {
            Handler().post { // If we do not use a handler here, it will not be possible
                // to use the menuItem after dismissing the searchView
                searchPreferenceMenuItem.expandActionView()
                searchPreferenceActionView!!.setQuery(savedInstanceSearchQuery, false)
            }
        }
        return true
    }

    override fun onSearchResultClicked(result: SearchPreferenceResult) {
        searchPreferenceActionView!!.cancelSearch()
        searchPreferenceMenuItem!!.collapseActionView()
        result.highlight(prefsFragment!!)
    }

    override fun onBackPressed() {
        if (!searchPreferenceActionView!!.cancelSearch()) {
            super.onBackPressed()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_SEARCH_QUERY, searchPreferenceActionView!!.query.toString())
        outState.putBoolean(KEY_SEARCH_ENABLED, !searchPreferenceActionView!!.isIconified)
        searchPreferenceActionView!!.cancelSearch()
        super.onSaveInstanceState(outState)
    }

    class PrefsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            val searchPreference =
                findPreference<Preference>("searchPreference") as SearchPreference?
            searchPreference!!.isVisible = false
        }
    }

    companion object {
        private const val KEY_SEARCH_QUERY = "search_query"
        private const val KEY_SEARCH_ENABLED = "search_enabled"
    }
}
