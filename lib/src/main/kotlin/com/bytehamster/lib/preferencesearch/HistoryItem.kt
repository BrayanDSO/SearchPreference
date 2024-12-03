package com.bytehamster.lib.preferencesearch

internal class HistoryItem(@JvmField val term: String) : ListItem() {
    override val type: Int = TYPE

    override fun equals(other: Any?): Boolean {
        if (other is HistoryItem) {
            return other.term == term
        }
        return false
    }

    companion object {
        const val TYPE: Int = 1
    }
}
