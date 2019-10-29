package org.dahatu.apps.linearviewtest

import android.animation.Animator
import android.animation.Animator.*
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.mooveit.library.Fakeit

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.empty.view.*
import org.dahatu.apps.linearviewtest.data.App
import org.dahatu.apps.linearviewtest.data.Book
import org.dahatu.apps.linearviewtest.data.Music
import org.dahatu.libs.linearview.Item
import org.dahatu.libs.linearview.LinearView
import org.dahatu.libs.linearview.OnLoadMoreListener
import org.dahatu.libs.linearview.OnManageListener


class MainActivity : AppCompatActivity(), OnManageListener, OnDeleteItemListener {
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

        Fakeit.init()

        dlv.onManageListener(this)
        dlv.setCustomLoadMoreListener(false, object : OnLoadMoreListener {
            override fun start() {
                loading.let {
                    it.alpha = 0f
                    it.visibility = View.VISIBLE
                    it.animate().setDuration(300)
                        .alpha(1f)
                        .setListener(null)
                        .start()
                }
            }

            override fun finish() {
                loading.let {
                    it.animate().setDuration(300)
                        .alpha(0f)
                        .setListener(object : AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {

                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                it.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                            }
                        })
                        .start()
                }
            }
        })
        Worker(pause = false).execute();
    }

    var index: Long = 0

    fun createRandomItem(): Item {
        val l = listOf(Book.TYPE, Music.TYPE, App.TYPE)
        val i: Item = when (l.random()) {
            Book.TYPE -> Book.create(index++)
            Music.TYPE -> Music.create(index++)
            App.TYPE -> App.create(index++)
            else -> throw IllegalArgumentException("Item type is unknown or not implemented!")
        }
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
                item.isChecked = true
                true
            }
            R.id.orientation_vertical -> {
                dlv.setOrientation(LinearView.VERTICAL)
                item.isChecked = true
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

    override fun layout(type: Int): Int = when (type) {
        Book.TYPE -> R.layout.layout_book
        Music.TYPE -> R.layout.layout_music
        App.TYPE -> R.layout.layout_app
        else -> throw IllegalArgumentException("Type #$type is unknown or not implemented!")
    }


    override fun onBind(item: Item, view: View, position: Int) = when (item) {
        is Book -> Book.bind(item, view) {
            it.update()
            dlv.updateItemBy(it)
            Toast.makeText(this, "Book is updated with new information.", Toast.LENGTH_SHORT).show()
        }
        is Music -> Music.bind(item, view) {
            it.update()
            dlv.updateItemBy(it)
            Toast.makeText(this, "Music is updated with new information.", Toast.LENGTH_SHORT).show()
        }
        is App -> App.bind(item, view) {
            it.update()
            dlv.updateItemBy(it)
            Toast.makeText(this, "App is updated with new information.", Toast.LENGTH_SHORT).show()
        }
        else -> throw IllegalArgumentException("Type #${item.type()} is unknown or not implemented!")
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

        private val list = mutableListOf<Item>()

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

interface OnDeleteItemListener {
    fun delete(item: Item)
    fun edit(item: Item)
}
