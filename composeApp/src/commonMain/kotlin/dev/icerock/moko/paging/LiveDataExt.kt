/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map


fun <T> StateFlow<List<T>>.withLoadingItem(
    loading: StateFlow<Boolean>,
    itemFactory: () -> T
): Flow<List<T>> = combine(this, loading) { items, nextPageLoading ->
    if (nextPageLoading) {
        items + itemFactory()
    } else {
        items
    }
}

fun <T> StateFlow<List<T>>.withReachEndNotifier(
    action: (Int) -> Unit
): Flow<ReachEndNotifierList<T>> = map { list ->
    list.withReachEndNotifier(action)
}
