package org.dahatu.apps.linearviewtest.data

import android.view.View
import io.kimo.lib.faker.Faker
import kotlinx.android.synthetic.main.layout_color.view.*
import org.dahatu.apps.linearviewtest.onDeleteItemListener
import org.dahatu.libs.linearview.Item

class ItemColor(val color: Int, val id: Long, val listener: onDeleteItemListener? = null) : Item {

    companion object {
        const val TYPE_ID = 0

        fun bind(item: ItemColor, view: View, position: Int) {
            view.color.setBackgroundColor(item.color)
            view.delete.setOnClickListener { item.listener?.delete(item) }
            view.edit.setOnClickListener {
                item.listener?.edit(
                    ItemColor(
                        Faker.Color.randomColor(),
                        item.id,
                        item.listener
                    )
                )
            }
        }
    }

    override fun type(): Int = TYPE_ID
    override fun id(): Long = id
}