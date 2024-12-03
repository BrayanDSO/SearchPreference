package com.bytehamster.preferencesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bytehamster.lib.preferencesearch.SearchPreference
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener

/**
 * This file contains a minimal working example for the library
 */
class SimpleExample : AppCompatActivity(), SearchPreferenceResultListener {
    private var prefsFragment: PrefsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsFragment = PrefsFragment()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, prefsFragment!!).commit()
    }

    override fun onSearchResultClicked(result: SearchPreferenceResult) {
        result.closeSearchPage(this)
        result.highlight(prefsFragment!!)
    }

    class PrefsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            val searchPreference =
                findPreference<Preference>("searchPreference") as SearchPreference?
            val config = searchPreference!!.searchConfiguration
            config.setActivity((activity as AppCompatActivity?)!!)
            config.index(R.xml.preferences)
        }
    }
}
