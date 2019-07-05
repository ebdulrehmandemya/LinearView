package org.dahatu.apps.linearviewtest

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.kimo.lib.faker.Faker

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_text.view.*
import org.dahatu.apps.linearviewtest.data.ItemColor
import org.dahatu.apps.linearviewtest.data.ItemNumber
import org.dahatu.apps.linearviewtest.data.ItemText
import org.dahatu.libs.linearview.Item
import org.dahatu.libs.linearview.LinearView
import org.dahatu.libs.linearview.OnManageListener


class MainActivity : AppCompatActivity(), OnManageListener, onDeleteItemListener {

    companion object {
        const val ERROR_PAGE_CODE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            dlv.addItem(createRandomItem(), 0)
            dlv.scrollToPosition(0)
        }

        Faker.with(this)

        dlv.onManageListener(this)
        Worker(pause = false).execute();
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
                Worker(pause = false).execute();
                true
            }
            R.id.action_custom_page -> {
                dlv.showPage(ERROR_PAGE_CODE)
                true
            }
            R.id.action_reverse -> {
                dlv.setReverseLayout(!dlv.isReverseLayout())
                true
            }
            R.id.orientation_horizontal -> {
                dlv.setOrientation(LinearView.HORIZONTAL)
                item.setChecked(true)
                true
            }
            R.id.orientation_vertical -> {
                dlv.setOrientation(LinearView.VERTICAL)
                item.setChecked(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun emptyLayout(): Int? = R.layout.empty

    override fun preloadLayout(): Int? = R.layout.preload

    override fun hasMore(): Boolean = true

    override fun onMore() {
        Worker().execute()
    }

    override fun layout(type: Int): Int {
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

    override fun onPageLayout(code: Int): Int? {
        return R.layout.empty
    }

    override fun onPageBind(code: Int, view: View) {
        if (code == LinearView.EMPTY_LAYOUT_CODE) {
            view.text.text = "***********************"
        } else if (code == ERROR_PAGE_CODE) {
            view.text.text = "<- Error : $code ->"
            view.text.setTextColor(Color.RED)
        }
    }

    override fun delete(item: Item) {
        dlv.removeBy(item)
    }

    override fun edit(item: Item) {
        dlv.updateItemBy(item)
    }

    @SuppressLint("StaticFieldLeak")
    inner class Worker(val number: Int = 15, val pause: Boolean = true) : AsyncTask<Void, Item, Boolean>() {

        val list = mutableListOf<Item>()
        var first = true

        override fun onProgressUpdate(vararg values: Item?) {
            val i = values[0] as Item
            list.add(i)
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            if (pause) Thread.sleep(2000)
            for (i in 0 until number)
                publishProgress(createRandomItem())
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            dlv.addItems(list)
        }

    }
}

interface onDeleteItemListener {
    fun delete(item: Item)
    fun edit(item: Item)
}
