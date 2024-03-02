/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging

class LambdaPagedListDataSource<T>(
    private val loadPageLambda: suspend (List<T>?) -> List<T>
) : PagedListDataSource<T> {
    override suspend fun loadPage(currentList: List<T>?): List<T> {
        return loadPageLambda(currentList)
    }
}
