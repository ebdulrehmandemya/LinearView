package org.dahatu.libs.linearview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ItemAdapter(val dlv: LinearView) : RecyclerView.Adapter<ItemAdapter.ItemVH>() {

    private val items = mutableListOf<Item>()
    internal var notItemAddedYet = true
    public var disableLoadMore = false
        get() = field
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val layout = when (viewType) {
            LoadMore.LOAD_MORE_TYPE_ID -> dlv.dl?.loadMoreLayout() ?: R.layout.dlv_load_more
            else -> dlv.dl!!.layout(viewType)
        }
        val view = LayoutInflater.from(dlv.context).inflate(layout, parent, false)
        return ItemVH(view)
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        if (items[position].type() == LoadMore.LOAD_MORE_TYPE_ID) return
        dlv.dl!!.onBind(items[position], holder.itemView, holder.adapterPosition)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].id()

    override fun getItemViewType(position: Int): Int = items[position].type()

    fun getItemAt(position: Int): Item? = items.getOrNull(position)

    fun getItem(type: Int, id: Long): Item? {
        for (i in 0 until items.size) {
            val item = items[i]
            if (item.type() == type && item.id() == id) return item
        }
        return null
    }

    fun getItem(compare: Comparable<Item>): Item? {
        for (i in 0 until items.size) {
            val item = items[i]
            if (compare.compareTo(item) == 0) return item
        }
        return null
    }

    fun getPosition(type: Int, id: Long): Int? {
        for (i in 0 until items.size) {
            val item = items[i]
            if (item.type() == type && item.id() == id) return i
        }
        return null
    }

    fun getPosition(compare: Comparable<Item>): Int? {
        for (i in 0 until items.size) {
            val item = items[i]
            if (compare.compareTo(item) == 0) return i
        }
        return null
    }

    fun reset() {
        items.clear()
        notItemAddedYet = true
        notifyDataSetChanged()
    }

    @JvmOverloads
    fun add(item: Item, index: Int? = null) {
        if (disableLoadMore && item.type() == LoadMore.LOAD_MORE_TYPE_ID) return
        addAll(listOf(item), index)
    }

    @JvmOverloads
    fun addAll(newItems: Collection<Item>, index: Int? = null) {
        val pos: Int = if (index != null && index >= 0) index else items.size
        items.addAll(pos, newItems)
        notItemAddedYet = false
        notifyItemRangeInserted(pos, newItems.size)
    }

    fun remove(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeLastItem() {
        if (items.size > 0)
            remove(items.size - 1)
    }

    fun removeLoadMore() {
        if (items.size > 0) {
            val index = items.size - 1
            val item = items[index]
            if (item.type() == LoadMore.LOAD_MORE_TYPE_ID)
                remove(index)
        }
    }

    fun update(position: Int, item: Item) {
        items[position] = item
        notifyItemChanged(position)
    }

    class ItemVH(view: View) : RecyclerView.ViewHolder(view)
}