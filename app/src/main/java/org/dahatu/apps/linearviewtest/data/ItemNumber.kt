package org.dahatu.apps.linearviewtest.data

import android.view.View
import io.kimo.lib.faker.Faker
import kotlinx.android.synthetic.main.layout_number.view.*
import kotlinx.android.synthetic.main.layout_number.view.delete
import org.dahatu.apps.linearviewtest.onDeleteItemListener
import org.dahatu.libs.linearview.Item

class ItemNumber(val number: Double, val id: Long, val listener: onDeleteItemListener? = null) : Item {

    companion object {
        const val TYPE_ID = 2

        fun bind(item: ItemNumber, view: View, position: Int) {
            view.number.text = item.number.toString()
            view.delete.setOnClickListener { item.listener?.delete(item) }
            view.edit.setOnClickListener {
                item.listener?.edit(
                    ItemNumber(
                        Faker.Number.randomNumber().toDouble(),
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