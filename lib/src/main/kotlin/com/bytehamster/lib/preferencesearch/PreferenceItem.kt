package com.bytehamster.lib.preferencesearch

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.TextUtils
import androidx.annotation.XmlRes
import com.bytehamster.lib.preferencesearch.Breadcrumb.concat
import org.apache.commons.text.similarity.FuzzyScore
import java.util.Locale

class PreferenceItem : ListItem, Parcelable {
    override val type: Int = TYPE

    @JvmField
    var title: String? = null

    @JvmField
    var summary: String? = null

    @JvmField
    var key: String? = null

    @JvmField
    var entries: String? = null

    @JvmField
    var breadcrumbs: String? = null

    @JvmField
    var keywords: String? = null

    @JvmField
    var keyBreadcrumbs: ArrayList<String> = ArrayList()

    @JvmField
    var resId: Int = 0

    private var lastScore = 0f
    private var lastKeyword: String? = null

    internal constructor()

    private constructor(`in`: Parcel) {
        this.title = `in`.readString()
        this.summary = `in`.readString()
        this.key = `in`.readString()
        this.breadcrumbs = `in`.readString()
        this.keywords = `in`.readString()
        this.resId = `in`.readInt()
    }

    fun hasData(): Boolean {
        return title != null || summary != null
    }

    fun matchesFuzzy(keyword: String): Boolean {
        return getScore(keyword) > 0.3
    }

    fun matches(keyword: String): Boolean {
        val locale = Locale.getDefault()
        return info.lowercase(locale).contains(keyword.lowercase(locale))
    }

    fun getScore(keyword: String): Float {
        if (TextUtils.isEmpty(keyword)) {
            return 0F
        } else if (TextUtils.equals(lastKeyword, keyword)) {
            return lastScore
        }
        val info = info

        val score = fuzzyScore.fuzzyScore(info, "ø$keyword").toFloat()
        val maxScore =
            ((keyword.length + 1) * 3 - 2).toFloat() // First item can not get +2 bonus score

        lastScore = score / maxScore
        lastKeyword = keyword
        return lastScore
    }

    private val info: String
        get() {
            val infoBuilder = StringBuilder()
            if (!TextUtils.isEmpty(title)) {
                infoBuilder.append("ø").append(title)
            }
            if (!TextUtils.isEmpty(summary)) {
                infoBuilder.append("ø").append(summary)
            }
            if (!TextUtils.isEmpty(entries)) {
                infoBuilder.append("ø").append(entries)
            }
            if (!TextUtils.isEmpty(breadcrumbs)) {
                infoBuilder.append("ø").append(breadcrumbs)
            }
            if (!TextUtils.isEmpty(keywords)) {
                infoBuilder.append("ø").append(keywords)
            }
            return infoBuilder.toString()
        }

    @Suppress("unused")
    fun withKey(key: String?): PreferenceItem {
        this.key = key
        return this
    }

    @Suppress("unused")
    fun withSummary(summary: String?): PreferenceItem {
        this.summary = summary
        return this
    }

    @Suppress("unused")
    fun withTitle(title: String?): PreferenceItem {
        this.title = title
        return this
    }

    @Suppress("unused")
    fun withEntries(entries: String?): PreferenceItem {
        this.entries = entries
        return this
    }

    @Suppress("unused")
    fun withKeywords(keywords: String?): PreferenceItem {
        this.keywords = keywords
        return this
    }

    @Suppress("unused")
    fun withResId(
        @XmlRes resId: Int,
    ): PreferenceItem {
        this.resId = resId
        return this
    }

    /**
     * @param breadcrumb The breadcrumb to add
     * @return For chaining
     */
    @Suppress("unused")
    fun addBreadcrumb(breadcrumb: String): PreferenceItem {
        this.breadcrumbs = concat(this.breadcrumbs, breadcrumb)
        return this
    }

    override fun toString(): String {
        return "PreferenceItem: $title $summary $key"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(
        parcel: Parcel,
        i: Int,
    ) {
        parcel.writeString(title)
        parcel.writeString(summary)
        parcel.writeString(key)
        parcel.writeString(breadcrumbs)
        parcel.writeString(keywords)
        parcel.writeInt(resId)
    }

    companion object {
        const val TYPE: Int = 2
        private val fuzzyScore get() = FuzzyScore(Locale.getDefault())

        @JvmField
        @Suppress("unused")
        val CREATOR: Creator<PreferenceItem> =
            object : Creator<PreferenceItem> {
                override fun createFromParcel(`in`: Parcel): PreferenceItem {
                    return PreferenceItem(`in`)
                }

                override fun newArray(size: Int): Array<PreferenceItem?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
