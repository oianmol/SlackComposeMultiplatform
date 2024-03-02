/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

@Suppress("TooManyFunctions")
class ReachEndNotifierList<T>(
    private val mWrappedList: List<T>,
    val onReachEnd: (Int) -> Unit
) : List<T> {

    override val size: Int = mWrappedList.size

    override fun contains(element: T): Boolean = mWrappedList.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = mWrappedList.containsAll(elements)

    override fun get(index: Int): T = mWrappedList[index]

    override fun indexOf(element: T): Int = mWrappedList.indexOf(element)

    override fun isEmpty(): Boolean = mWrappedList.isEmpty()

    override fun iterator(): Iterator<T> = mWrappedList.iterator()

    override fun lastIndexOf(element: T): Int = mWrappedList.lastIndexOf(element)

    override fun listIterator(): ListIterator<T> = mWrappedList.listIterator()

    override fun listIterator(index: Int): ListIterator<T> = mWrappedList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        mWrappedList.subList(fromIndex, toIndex)

    fun notifyReachEnd() {
        onReachEnd(mWrappedList.lastIndex)
    }
}

fun <T> List<T>.withReachEndNotifier(action: (Int) -> Unit): ReachEndNotifierList<T> =
    ReachEndNotifierList(
        this,
        action
    )
