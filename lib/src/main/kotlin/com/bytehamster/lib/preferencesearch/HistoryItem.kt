package com.bytehamster.lib.preferencesearch

internal class HistoryItem(
    @JvmField val term: String,
) : ListItem() {
    override val type: Int = TYPE

    override fun equals(other: Any?): Boolean {
        if (other is HistoryItem) {
            return other.term == term
        }
        return false
    }

    override fun hashCode(): Int {
        var result = term.hashCode()
        result = 31 * result + type
        return result
    }

    companion object {
        const val TYPE: Int = 1
    }
}
