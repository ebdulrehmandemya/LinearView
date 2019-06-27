package org.dahatu.apps.linearviewtest

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.kimo.lib.faker.Faker

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.dahatu.apps.linearviewtest.data.ItemColor
import org.dahatu.apps.linearviewtest.data.ItemNumber
import org.dahatu.apps.linearviewtest.data.ItemText
import org.dahatu.libs.linearview.Item
import org.dahatu.libs.linearview.OnManageListener


class MainActivity : AppCompatActivity(), OnManageListener, onDeleteItemListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            dlv.addItem(createRandomItem(), 0)
            dlv.smoothScrollToPosition(0)
        }

        Faker.with(this)

        dlv.onManageListener(this)
        dlv.setSmoothScroll(true)

    }

    var index: Long = 0

    fun createRandomItem(): Item {
        val l = listOf(0, 1, 2)
        val i: Item = when (l.random()) {
            ItemColor.TYPE_ID -> ItemColor(Faker.Color.randomColor(), index, this)
            ItemNumber.TYPE_ID -> ItemNumber(Faker.Number.randomNumber().toDouble(), index, this)
            ItemText.TYPE_ID -> ItemText(Faker.Name.randomText(), index, this)
            else -> throw TypeCastException("Item type is unknown or not implemented!")
        }
        index++
        return i;
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_reload -> {
                dlv.clearItems()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onEmpty(): Int? = R.layout.empty

    override fun onPreload(): Int? = R.layout.preload

    override fun onLayout(type: Int): Int {
        when (type) {
            ItemColor.TYPE_ID -> return R.layout.layout_color
            ItemNumber.TYPE_ID -> return R.layout.layout_number
            ItemText.TYPE_ID -> return R.layout.layout_text
            else -> throw TypeCastException("Type #$type is unknown or not implemented!")
        }
    }

    override fun onBind(item: Item, view: View, position: Int) {
        val p = dlv.itemPositionBy(object : Comparable<Item> {
            override fun compareTo(other: Item): Int = if (other.id() == item.id()) 0 else -1
        }) ?: return
        when (item) {
            is ItemColor -> ItemColor.bind(item, view, p)
            is ItemNumber -> ItemNumber.bind(item, view, p)
            is ItemText -> ItemText.bind(item, view, p)
        }
    }

    override fun delete(item: Item) {
        dlv.removeBy(item)
    }

    override fun edit(item: Item) {
        dlv.updateItemBy(item)
    }
}

interface onDeleteItemListener {
    fun delete(item: Item)
    fun edit(item: Item)
}
