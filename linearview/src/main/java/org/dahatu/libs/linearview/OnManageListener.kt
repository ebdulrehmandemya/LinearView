package org.dahatu.libs.linearview

import android.view.View
import androidx.annotation.LayoutRes

interface OnManageListener {
    @LayoutRes
    fun layout(type: Int): Int

    fun onBind(item: Item, view: View, position: Int)

    @LayoutRes
    fun preloadLayout(): Int? = null

    @LayoutRes
    fun emptyLayout(): Int? = null

    @LayoutRes
    fun loadMoreLayout(): Int? = null

    fun hasMore(): Boolean = false

    fun onMore() {}

    @LayoutRes
    fun onPageLayout(code: Int): Int? = null

    fun onPageBind(code: Int, view: View) {}
}