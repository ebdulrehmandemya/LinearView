package org.dahatu.apps.linearviewtest.data

import android.view.View
import com.mooveit.library.Fakeit
import kotlinx.android.synthetic.main.layout_book.view.*
import org.dahatu.libs.linearview.Item

class Book(val id: Long) : Item {
    companion object {

        const val TYPE = 0

        fun create(id: Long) = Book(id).apply {
            title = Fakeit.book().title()
            author = Fakeit.book().author()
            publisher = Fakeit.book().publisher()
            gender = Fakeit.book().genre()
        }

        fun bind(book: Book, view: View, onUpdate: (book: Book) -> Unit) {
            view.title.text = book.title
            view.publisher.text = book.publisher
            view.author.text = book.author
            view.gender.text = book.gender
            view.setOnClickListener { onUpdate(book) }
        }
    }

    private lateinit var title: String
    private lateinit var author: String
    private lateinit var publisher: String
    private lateinit var gender: String

    override fun type(): Int = TYPE
    override fun id(): Long = id

    fun update() {
        title = Fakeit.book().title()
        author = Fakeit.book().author()
        publisher = Fakeit.book().publisher()
        gender = Fakeit.book().genre()
    }

}