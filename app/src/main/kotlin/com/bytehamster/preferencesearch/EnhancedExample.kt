package com.bytehamster.preferencesearch

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bytehamster.lib.preferencesearch.SearchPreference
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener

/**
 * This file demonstrates some additional features that might not be needed when setting it up for the first time
 */
class EnhancedExample : AppCompatActivity(), SearchPreferenceResultListener {
    private var prefsFragment: PrefsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        prefsFragment = PrefsFragment()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, prefsFragment!!).commit()
    }

    override fun onSearchResultClicked(result: SearchPreferenceResult) {
        prefsFragment = PrefsFragment()
        supportFragmentManager.commit {
            replace(android.R.id.content, prefsFragment!!).addToBackStack("PrefsFragment")
        }
        Handler().post { prefsFragment!!.onSearchResultClicked(result) }
    }

    class PrefsFragment : PreferenceFragmentCompat(), SearchPreferenceResultListener {
        private var searchPreference: SearchPreference? = null

        override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?,
        ) {
            addPreferencesFromResource(R.xml.preferences)

            searchPreference = findPreference<Preference>("searchPreference") as SearchPreference?
            val config = searchPreference!!.searchConfiguration
            config.setOnSearchListener { searchPreferenceFragment ->
                parentFragmentManager.commit {
                    replace(android.R.id.content, searchPreferenceFragment)
                    addToBackStack(null)
                }
            }
            config.index(R.xml.preferences).addBreadcrumb("Main file")
            config.index(R.xml.preferences2).addBreadcrumb("Second file")
            config.setBreadcrumbsEnabled(true)
            config.setHistoryEnabled(true)
            config.setFuzzySearchEnabled(true)
        }

        override fun onSearchResultClicked(result: SearchPreferenceResult) {
            parentFragmentManager.popBackStack()
            if (result.resourceFile == R.xml.preferences) {
                searchPreference!!.isVisible = false // Do not allow to click search multiple times
                scrollToPreference(result.key)
                findPreference<Preference>(result.key)!!.title =
                    "RESULT: " +
                    findPreference<Preference>(
                        result.key,
                    )!!.title
            } else {
                // Result was found in the other file
                preferenceScreen.removeAll()
                addPreferencesFromResource(R.xml.preferences2)
                result.highlight(this)
            }
        }
    }
}
