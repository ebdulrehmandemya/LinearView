package org.dahatu.apps.linearviewtest.data

import android.view.View
import com.mooveit.library.Fakeit
import kotlinx.android.synthetic.main.layout_app.view.*
import org.dahatu.libs.linearview.Item

class App(val id: Long) : Item {
    companion object {

        const val TYPE = 2

        fun create(id: Long) = App(id).apply {
            name = Fakeit.app().name()
            developer = Fakeit.app().author()
            version = Fakeit.app().version()
        }

        fun bind(app: App, view: View) {
            view.name.text = app.name
            view.developer.text = app.developer
            view.version.text = app.version
        }
    }

    private lateinit var name: String
    private lateinit var developer: String
    private lateinit var version: String

    override fun type(): Int = TYPE
    override fun id(): Long = id
}