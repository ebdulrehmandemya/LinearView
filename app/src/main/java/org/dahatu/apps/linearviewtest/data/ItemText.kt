package org.dahatu.apps.linearviewtest.data

import android.view.View
import io.kimo.lib.faker.Faker
import kotlinx.android.synthetic.main.layout_text.view.*
import kotlinx.android.synthetic.main.layout_text.view.delete
import org.dahatu.apps.linearviewtest.onDeleteItemListener
import org.dahatu.libs.linearview.Item

class ItemText(val text: String, val id: Long, val listener: onDeleteItemListener? = null) : Item {

    companion object {
        const val TYPE_ID = 1

        fun bind(item: ItemText, view: View, position: Int) {
            view.text.text = item.text
            view.delete.setOnClickListener { item.listener?.delete(item) }
            view.edit.setOnClickListener {
                item.listener?.edit(
                    ItemText(
                        Faker.Name.randomText(),
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