package com.bytehamster.lib.preferencesearch

import android.text.TextUtils

internal object Breadcrumb {
    /**
     * Joins two breadcrumbs
     * @param s1 First breadcrumb, might be null
     * @param s2 Second breadcrumb
     * @return Both breadcrumbs joined
     */
    @JvmStatic
    fun concat(s1: String?, s2: String): String {
        if (TextUtils.isEmpty(s1)) {
            return s2
        }
        return "$s1 > $s2"
    }
}
