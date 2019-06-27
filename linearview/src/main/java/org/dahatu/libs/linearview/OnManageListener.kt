package org.dahatu.libs.linearview

import android.view.View
import androidx.annotation.LayoutRes

interface OnManageListener {
    @LayoutRes
    fun onLayout(type: Int): Int

    @LayoutRes
    fun onPreload(): Int? = null

    @LayoutRes
    fun onEmpty(): Int? = null

    fun onBind(item: Item, view: View, position: Int)

    fun hasMore(): Boolean = false

    fun onMore(): Nothing? = null

    @LayoutRes
    fun onPageLayout(code: Int): Int? = null

    fun onPageBind(code: Int, view: View): Nothing? = null
}