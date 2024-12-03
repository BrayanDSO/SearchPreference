package com.bytehamster.lib.preferencesearch

import android.content.Context
import android.graphics.PorterDuff
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup.PreferencePositionCallback
import androidx.recyclerview.widget.RecyclerView

class SearchPreferenceResult internal constructor(
    /**
     * Returns the key of the preference pressed
     * @return The key
     */
    @JvmField val key: String,
    /**
     * Returns the file in which the result was found
     * @return The file in which the result was found
     */
    val resourceFile: Int
) {
    /**
     * Highlight the preference that was found
     * @param prefsFragment Fragment that contains the preference
     */
    fun highlight(prefsFragment: PreferenceFragmentCompat) {
        Handler().post { doHighlight(prefsFragment) }
    }

    private fun doHighlight(prefsFragment: PreferenceFragmentCompat) {
        val prefResult = prefsFragment.findPreference<Preference>(
            key
        )

        if (prefResult == null) {
            Log.e("doHighlight", "Preference not found on given screen")
            return
        }
        val recyclerView = prefsFragment.listView
        val adapter = recyclerView.adapter
        if (adapter is PreferencePositionCallback) {
            val callback = adapter as PreferencePositionCallback
            val position = callback.getPreferenceAdapterPosition(prefResult)
            if (position != RecyclerView.NO_POSITION) {
                recyclerView.scrollToPosition(position)
                recyclerView.postDelayed({
                    val holder = recyclerView.findViewHolderForAdapterPosition(position)
                    if (holder != null) {
                        val oldBackground = holder.itemView.background
                        val color = getColorFromAttr(
                            prefsFragment.requireContext(),
                            android.R.attr.textColorPrimary
                        )
                        holder.itemView.setBackgroundColor(color and 0xffffff or 0x33000000)
                        Handler().postDelayed(
                            { holder.itemView.setBackgroundDrawable(oldBackground) },
                            1000
                        )
                        return@postDelayed
                    }
                    highlightFallback(prefsFragment, prefResult)
                }, 200)
                return
            }
        }
        highlightFallback(prefsFragment, prefResult)
    }

    /**
     * Alternative highlight method if accessing the view did not work
     */
    private fun highlightFallback(prefsFragment: PreferenceFragmentCompat, prefResult: Preference) {
        val oldIcon = prefResult.icon
        val oldSpaceReserved = prefResult.isIconSpaceReserved
        val arrow = AppCompatResources.getDrawable(
            prefsFragment.requireContext(),
            R.drawable.searchpreference_ic_arrow_right
        )
        val color = getColorFromAttr(prefsFragment.requireContext(), android.R.attr.textColorPrimary)
        arrow!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        prefResult.icon = arrow
        prefsFragment.scrollToPreference(prefResult)
        Handler().postDelayed({
            prefResult.icon = oldIcon
            prefResult.isIconSpaceReserved = oldSpaceReserved
        }, 1000)
    }

    private fun getColorFromAttr(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        val arr = context.obtainStyledAttributes(
            typedValue.data,
            intArrayOf(android.R.attr.textColorPrimary)
        )
        val color = arr.getColor(0, -0xc0ae4b)
        arr.recycle()
        return color
    }

    /**
     * Closes the search results page
     * @param activity The current activity
     */
    fun closeSearchPage(activity: AppCompatActivity) {
        val fm = activity.supportFragmentManager
        fm.beginTransaction().remove(fm.findFragmentByTag(SearchPreferenceFragment.TAG)!!).commit()
    }
}
