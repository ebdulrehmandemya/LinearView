package org.dahatu.libs.linearview

import android.annotation.TargetApi

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class LinearView : FrameLayout {

    companion object {
        const val EMPTY_LAYOUT_CODE = Int.MIN_VALUE
        const val PRELOAD_LAYOUT_CODE = Int.MIN_VALUE + 1

        const val VERTICAL = RecyclerView.VERTICAL
        const val HORIZONTAL = RecyclerView.HORIZONTAL
    }

    private lateinit var rv: RecyclerView
    private lateinit var ia: ItemAdapter
    internal var dl: OnManageListener? = null
    private var isLoading = false
    private var ipp = 0

    constructor(context: Context)
            : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        var orientation = VERTICAL
        var reverse = false

        attrs?.let {
            val ta = context.theme.obtainStyledAttributes(it, R.styleable.LinearView, 0, 0)
            orientation = ta.getInteger(R.styleable.LinearView_orientation, VERTICAL)
            reverse = ta.getBoolean(R.styleable.LinearView_reverse, false)
            ta.recycle()
        }
        ia = ItemAdapter(this)
        rv = RecyclerView(context)
        rv.let {
            it.id = ViewCompat.generateViewId()
            it.adapter = ia
            it.layoutManager = LinearLayoutManager(context, orientation, reverse)
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    scrollLoadMore(dx, dy)
                }
            })
            addView(it, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
        isLoading = false
        updateUI()
    }

    private fun scrollLoadMore(dx: Int, dy: Int) {
        val lm = rv.layoutManager as LinearLayoutManager
        if (lm.orientation == VERTICAL) {
            if (dy < 0) return
        } else {
            if (dx < 0) return
        }
        val ic = ia.itemCount
        if ((ipp > 0) && (ic < ipp)) return
        dl!!.let {
            if (!it.hasMore()) return
            val vic = lm.childCount
            val tic = lm.itemCount
            val lvi = lm.findLastCompletelyVisibleItemPosition()
            if (!isLoading && (tic <= (lvi + vic))) {
                startLoading()
                it.onMore()
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        rv.scrollTo(x, y)
    }

    override fun scrollBy(x: Int, y: Int) {
        rv.scrollTo(x, y)
    }

    fun scrollToPosition(position: Int) {
        rv.scrollToPosition(position)
    }


    fun smoothScrollToPosition(position: Int) {
        rv.smoothScrollToPosition(position)
    }

    fun startLoading() = post {
        isLoading = true
        ia.add(LoadMore.create())
    }

    fun hideLoading() = post {
        if (!isLoading) return@post
        ia.removeLastItem()
        isLoading = false
    }

    fun recyclerView() = rv

    fun adapter() = ia

    @JvmOverloads
    fun addItem(item: Item, index: Int? = null) {
        addItems(listOf(item), index)
    }

    @JvmOverloads
    fun addItems(items: Collection<Item>, index: Int? = null) {
        if (isLoading) hideLoading()
        ia.addAll(items, index)
        updateUI()
    }


    fun updateUI() = post {
        if (ia.notItemAddedYet)
            dl?.let {
                showLayout(it.preloadLayout(), PRELOAD_LAYOUT_CODE)
            }
        else if (ia.itemCount == 0)
            dl?.let {
                showLayout(it.emptyLayout(), EMPTY_LAYOUT_CODE)
            }
        else {
            val child = getChildAt(0)
            if (child is RecyclerView) return@post
            showView(rv)
        }
    }

    private fun showView(to: View) {
        removeAllViews()
        to.alpha = 0f
        addView(to)
        to.animate().alpha(1f).setDuration(500).start()
    }

    private fun showLayout(layout: Int?, code: Int? = null) {
        layout?.let {
            val view = LayoutInflater.from(context).inflate(layout, this, false)
            code?.let {
                dl?.onPageBind(code, view)
            }
            showView(view)
        }
    }

    fun showPage(code: Int) {
        val layout = dl?.onPageLayout(code)
        showLayout(layout, code)
    }

    fun onManageListener(listener: OnManageListener) {
        dl = listener
        updateUI()
    }

    fun setItemsPerPage(ipp: Int) {
        this.ipp = ipp
    }

    fun setPrefetchItemCount(itemCount: Int) {
        (rv.layoutManager as LinearLayoutManager).initialPrefetchItemCount = itemCount
    }

    fun setOrientation(orientation: Int) {
        (rv.layoutManager as LinearLayoutManager).orientation = orientation
    }

    fun getOrientation() = (rv.layoutManager as LinearLayoutManager).orientation

    fun setReverseLayout(reverse: Boolean) {
        (rv.layoutManager as LinearLayoutManager).reverseLayout = reverse
    }

    fun isReverseLayout(): Boolean = (rv.layoutManager as LinearLayoutManager).reverseLayout

    fun addItemDecoration(decoration: RecyclerView.ItemDecoration) =
        rv.addItemDecoration(decoration)

    fun clearItems() = post {
        ia.reset()
        updateUI()
        isLoading = false
    }

    fun removeAt(position: Int) {
        ia.remove(position)
        updateUI()
    }

    fun removeBy(item: Item) {
        val p = itemPositionBy(item)
        p?.let {
            ia.remove(it)
            updateUI()
        }
    }

    fun removeBy(type: Int, id: Long) {
        val p = itemPositionBy(type, id)
        p?.let {
            ia.remove(it)
            updateUI()
        }
    }

    fun removeBy(compare: Comparable<Item>) {
        val p = itemPositionBy(compare)
        p?.let {
            ia.remove(it)
            updateUI()
        }
    }

    fun itemAt(position: Int): Item? = ia.getItemAt(position)

    fun itemBy(item: Item): Item? = ia.getItem(item.type(), item.id())

    fun itemBy(type: Int, id: Long): Item? = ia.getItem(type, id)

    fun itemBy(compare: Comparable<Item>): Item? = ia.getItem(compare)

    fun itemPositionBy(item: Item): Int? = ia.getPosition(item.type(), item.id())

    fun itemPositionBy(type: Int, id: Long): Int? = ia.getPosition(type, id)

    fun itemPositionBy(compare: Comparable<Item>): Int? = ia.getPosition(compare)

    fun updateItemAt(position: Int, item: Item) = ia.update(position, item)

    fun updateItemBy(item: Item) {
        val p = itemPositionBy(item)
        p?.let {
            ia.update(it, item)
        }
    }

    fun updateItemBy(item: Item, compare: Comparable<Item>) {
        val p = itemPositionBy(compare)
        p?.let {
            ia.update(it, item)
        }
    }


    fun notifyUpdatePositions() = ia.notifyDataSetChanged()

}