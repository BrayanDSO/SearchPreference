package com.bytehamster.lib.preferencesearch

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager

class SearchPreferenceActionView : SearchView {
    protected var searchFragment: SearchPreferenceFragment? = null
    var searchConfiguration: SearchConfiguration = SearchConfiguration()
        protected set
    var activity: AppCompatActivity? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        searchConfiguration.setSearchBarEnabled(false)
        setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (searchFragment != null) {
                    searchFragment!!.setSearchTerm(newText)
                }
                return true
            }
        })
        setOnQueryTextFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (hasFocus && (searchFragment == null || !searchFragment!!.isVisible)) {
                searchFragment = searchConfiguration.showSearchFragment()
                searchFragment!!.setHistoryClickListener { entry: String ->
                    setQuery(entry, false)
                }
            }
        }
    }

    /**
     * Hides the search fragment
     * @return true if it was hidden, so the calling activity should not go back itself.
     */
    fun cancelSearch(): Boolean {
        setQuery("", false)

        var didSomething = false
        if (!isIconified) {
            isIconified = true
            didSomething = true
        }
        if (searchFragment != null && searchFragment!!.isVisible) {
            removeFragment()

            /*
            AnimationUtils.startCircularExitAnimation(getContext(), searchFragment.getView(),
                    getSearchConfiguration().getRevealAnimationSetting(),
                    new AnimationUtils.OnDismissedListener() {
                @Override
                public void onDismissed() {
                    removeFragment();
                }
            });
            */
            didSomething = true
        }
        return didSomething
    }

    protected fun removeFragment() {
        if (searchFragment!!.isVisible) {
            val fm = activity!!.supportFragmentManager
            fm.beginTransaction().remove(searchFragment!!).commit()
            fm.popBackStack(SearchPreferenceFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}
