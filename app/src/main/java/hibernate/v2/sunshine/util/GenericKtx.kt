package hibernate.v2.sunshine.util

import java.util.Collections

fun <T> MutableList<T>.moveAt(oldIndex: Int, newIndex: Int) {
    val item = this[oldIndex]
    removeAt(oldIndex)
    if (oldIndex > newIndex)
        add(newIndex, item)
    else
        add(newIndex - 1, item)
}

/**
 * Swaps the position of two items at two respective indices
 */
fun <T> MutableList<T>.swap(indexOne: Int, indexTwo: Int) {
    Collections.swap(this, indexOne, indexTwo)
}